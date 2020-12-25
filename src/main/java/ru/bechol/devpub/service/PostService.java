package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.request.*;
import ru.bechol.devpub.response.*;
import ru.bechol.devpub.response.dto.PostDto;
import ru.bechol.devpub.service.enums.*;
import ru.bechol.devpub.service.exception.*;
import ru.bechol.devpub.service.helper.*;

import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static ru.bechol.devpub.service.helper.ErrorMapHelper.createBindingErrorResponse;

/**
 * Класс PostService.
 * Сервисный слой для Post.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Slf4j
@Service
public class PostService {

	private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private TagService tagService;
	@Autowired
	private Messages messages;
	@Autowired
	private PostMapperHelper postMapperHelper;
	@Autowired
	private GlobalSettingsService globalSettingsService;
	@Autowired
	private ModeratorLoadBalancer moderatorLoadBalancer;
	@Autowired
	private EmailService emailService;


	/**
	 * Метод preparePostCreationTime
	 * Проверяет время публикации поста. В случае, если время публикации раньше текущего времени,
	 * оно автоматически становиться текущим. Если позже текущего - устанавливается введенное значение.
	 *
	 * @param timestamp - введеное время публикации поста.
	 * @return LocalDateTime.
	 */
	private LocalDateTime preparePostCreationTime(long timestamp) {
		long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		return timestamp < now ? LocalDateTime.now() :
				LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
	}

	/**
	 * Метод getPostsWithSortMode.
	 * Формирует список постов для отображения в зависимости от заданного режима сортировки.
	 *
	 * @param offset   сдвиг от 0 для постраничного вывода.
	 * @param limit    количество постов, которое надо вывести.
	 * @param sortMode режим сортировки.
	 * @return PostResponse
	 */
	public PostResponse getPostsWithSortMode(int offset, int limit, SortMode sortMode) {
		Pageable defaultPageable = PageRequest.of(offset / limit, limit);
		switch (sortMode) {
			case EARLY:
				return this.getPostsByTimeSorting(offset, limit, Sort.by("time").ascending());
			case RECENT:
				return this.getPostsByTimeSorting(offset, limit, Sort.by("time").descending());
			case BEST:
				Page<Post> voteSortedPosts = postRepository.findBestPosts(defaultPageable);
				return this.createPostResponse(voteSortedPosts, true, false, false);
			case POPULAR:
				Page<Post> commentSortedPosts = postRepository.findPopularPosts(defaultPageable);
				return this.createPostResponse(commentSortedPosts, true, false, false);
		}
		return PostResponse.builder().count(0).posts(new ArrayList<>()).build();
	}

	/**
	 * Метод getPostsByTimeSorting.
	 * Формирование списка постов, отсортированных по времени создания поста.
	 *
	 * @param offset сдвиг от 0 для постраничного вывода.
	 * @param limit  количество постов, которое надо вывести.
	 * @param sort   способ сортировки.
	 * @return PostResponse.
	 */
	private PostResponse getPostsByTimeSorting(int offset, int limit, Sort sort) {
		Pageable pageable = PageRequest.of(offset / limit, limit, sort);
		Page<Post> postPages = postRepository.findByModerationStatusAndActiveTrueAndTimeBefore(
				ModerationStatus.ACCEPTED.name(), LocalDateTime.now(), pageable);
		return this.createPostResponse(postPages, true, false, false);
	}

	/**
	 * Метод createPostResponse.
	 * Формирование ответа создержащего отсортированный список постов.
	 *
	 * @param postPages       постраничный список постов.
	 * @param includeAnnounce включать в ответ аннотации постов.
	 * @param includeComments включать в ответ комментарии постов.
	 * @param includeTags     включать в ответ тегои постов.
	 * @return PostResponse.
	 */
	private PostResponse createPostResponse(Page<Post> postPages,
											boolean includeAnnounce, boolean includeComments, boolean includeTags) {
		List<PostDto> postDtoList = postMapperHelper.mapPostList(postPages.getContent(),
				includeAnnounce, includeComments, includeTags);
		return PostResponse.builder().count(postPages.getTotalElements()).posts(postDtoList).build();
	}

	/**
	 * Метод findPostsByTextContainingQuery.
	 * Метод находит все посты, текст которых содержит строку query.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода.
	 * @param limit  - количество постов, которое надо вывести.
	 * @param query  - поисковый запрос.
	 * @return PostResponse
	 */
	public PostResponse findPostsByTextContainingQuery(int offset, int limit, String query) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Post> postPages = postRepository
				.findByModerationStatusAndActiveTrueAndTimeBeforeAndTextContainingIgnoreCase(
						ModerationStatus.ACCEPTED.name(), LocalDateTime.now(), query, pageable);
		return this.createPostResponse(postPages, true, false, false);
	}

	/**
	 * Метод findPostsByDate.
	 * Выводит посты за указанную дату, переданную в запросе в параметре date.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода
	 * @param limit  - количество постов, которое надо вывести
	 * @param date   - дата, за которую необходимо отобрать посты.
	 * @return - PostResponse.
	 */
	public PostResponse findPostsByDate(int offset, int limit, String date) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Post> postPages = postRepository.findByDate(pageable, date);
		return this.createPostResponse(postPages, true, false, false);
	}

	/**
	 * Метод findByTag.
	 * Метод выводит список постов, привязанных к тегу, который был передан методу в качестве параметра tag.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода
	 * @param limit  - количество постов, которое надо вывести
	 * @param tag    - тег, к которому привязаны посты.
	 * @return - PostResponse.
	 */
	public PostResponse findByTag(int offset, int limit, String tag) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Post> postPages = postRepository.findByTag(pageable, tag);
		return this.createPostResponse(postPages, true, false, false);
	}

	/**
	 * Метод findActiveUserPosts.
	 * Метод формирует ответ на GET запрос /api/post/my.
	 *
	 * @param offset     - сдвиг от 0 для постраничного вывода
	 * @param limit      - количество постов, которое надо вывести
	 * @param postStatus - статус модерации.
	 * @return - PostResponse.
	 */
	public PostResponse findActiveUserPosts(Principal principal, int offset, int limit, PostStatus postStatus) {
		Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.ASC, "time");
		List<Post> myPostList = userService.findByEmail(principal.getName()).getPosts();
		switch (postStatus) {
			case PENDING:
				myPostList = this.filterActivePostByModerationStatus(myPostList, ModerationStatus.NEW);
				break;
			case DECLINED:
				myPostList = this.filterActivePostByModerationStatus(myPostList, ModerationStatus.DECLINED);
				break;
			case PUBLISHED:
				myPostList = this.filterActivePostByModerationStatus(myPostList, ModerationStatus.ACCEPTED);
				break;
			default:
				myPostList = myPostList.stream().filter(post -> !post.isActive()).collect(Collectors.toList());
		}
		List<PostDto> resultList = postMapperHelper.mapPostList(
				myPostList, true, false, false
		);
		Page<PostDto> postPages = new PageImpl<>(resultList, pageable, resultList.size());
		return PostResponse.builder().count(resultList.size()).posts(postPages.getContent()).build();
	}

	/**
	 * Метод filterActivePostByModerationStatus.
	 * Выборка активных постов по статусу модерации.
	 *
	 * @param postList         - лист постов, из которого выбираем.
	 * @param moderationStatus - статус модерации.
	 * @return - отфильтрованный список постов.
	 */
	private List<Post> filterActivePostByModerationStatus(List<Post> postList, ModerationStatus moderationStatus) {
		return postList.stream().filter(post ->
				post.isActive() && post.getModerationStatus().equals(moderationStatus.name()))
				.collect(Collectors.toList()
				);
	}

	/**
	 * Метод createNewPost.
	 * Создание поста.
	 *
	 * @param principal     - авторизованный пользователь.
	 * @param postRequest   - данные нового поста.
	 * @param bindingResult - результаты валидации данных нового поста.
	 * @return - ResponseEntity<Response<?>>.
	 */
	public ResponseEntity<?> createNewPost(Principal principal, PostRequest postRequest, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			return createBindingErrorResponse(bindingResult, HttpStatus.OK);
		}
		Post newPost = new Post();
		newPost.setTime(this.preparePostCreationTime(postRequest.getTimestamp()));
		newPost.setActive(postRequest.isActive());
		newPost.setTitle(postRequest.getTitle());
		newPost.setText(postRequest.getText());
		newPost.setModerationStatus(this.acceptModerationStatus());
		newPost.setUser(userService.findByEmail(principal.getName()));
		newPost.setModerator(moderatorLoadBalancer.appointModerator());
		if (postRequest.getTags().size() > 0) {
			newPost.setTags(tagService.mapTags(postRequest.getTags()));
		}
		this.savePost(newPost);
		return ResponseEntity.ok(Response.builder().result(true).build());
	}

    /**
     * Метод savePost.
     * Сохранение поста и при необходимости отправка уведомления модератору.
     * @param post пост, который необходимо сохранить,
     */
	@Async("asyncExecutor")
	public void savePost(Post post) {
        Optional.of(postRepository.save(post)).ifPresent(savedPost -> {
                    if (post.isActive() && savedPost.getModerationStatus().equals("NEW")) {
                        emailService.send(post.getModerator().getEmail(),
								messages.getMessage("post.moderation-mail-subject"),
								messages.getMessage("post.moderation-mail", post.getModerator().getName()));
                    }
                });
    }

	/**
	 * Метод acceptModerationStatus.
	 * Выбор статуса модерации в зависимости от настройки премодерации.
	 *
	 * @return ModerationStatus.NEW - если установлен режим премодерации.
	 * @throws CodeNotFoundException - если настрока не найдена по коду.
	 */
	private String acceptModerationStatus() throws CodeNotFoundException {
		if (globalSettingsService.checkSetting("POST_PREMODERATION", SettingValue.YES)) {
			return ModerationStatus.NEW.toString();
		} else {
			return ModerationStatus.ACCEPTED.toString();
		}
	}

	/**
	 * Метод showPost.
	 * Формирование ответа на запрос GET /api/post/{$ID}
	 *
	 * @param postId    - id поста.
	 * @param principal - авторизованный пользователь.
	 * @return ResponseEntity<PostDto>
	 */
	public PostDto showPost(long postId, Principal principal) throws PostNotFoundException {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
				messages.getMessage("warning.post.not-found")));
		User activeUser = null;
		if (principal != null) {
			activeUser = userService.findByEmail(principal.getName());
		} else {
			post = increaseViewCount(post);
			return postMapperHelper.mapPost(post, false, true, true);
		}
		boolean isModerator = activeUser.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_MODERATOR));
		boolean isAuthor = post.getUser().getId() == activeUser.getId();
		if (isAuthor || isModerator) {
			return postMapperHelper.mapPost(post, false, true, true);
		}
		post = increaseViewCount(post);
		return postMapperHelper.mapPost(post, false, true, true);
	}

	/**
	 * Метод increaseViewCount.
	 * Увеличение количества просмотров поста.
	 *
	 * @param post - пост.
	 * @return Post.
	 */
	private Post increaseViewCount(Post post) {
		post.setViewCount(post.getViewCount() + 1);
		return postRepository.save(post);
	}

	/**
	 * Метод findPostsOnModeration.
	 * Формирование ответа для запроса GET /api/post/moderation
	 *
	 * @param principal - авторизованный пользователь.
	 * @param offset    - сдвиг от 0 для постраничного вывода
	 * @param limit     - количество постов, которое надо вывести
	 * @param status    - статус модерации.
	 * @return PostResponse.
	 */
	public PostResponse findPostsOnModeration(Principal principal, int offset, int limit, String status)
			throws EnumValueNotFoundException {
		User moderator = userService.findByEmail(principal.getName());
		Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.ASC, "time");
		Page<Post> queryListResult;
		switch (ModerationStatus.fromValue(status)) {
			case ACCEPTED:
				queryListResult = postRepository.findByModeratedByAndModerationStatusAndActiveTrue(moderator,
						ModerationStatus.ACCEPTED.toString(), pageable);
				break;
			case DECLINED:
				queryListResult = postRepository.findByModeratedByAndModerationStatusAndActiveTrue(moderator,
						ModerationStatus.DECLINED.toString(), pageable);
				break;
			default:
				queryListResult = postRepository.
						findByModerationStatusAndActiveTrue(ModerationStatus.NEW.toString(), pageable);
		}
		List<PostDto> resultList = postMapperHelper.mapPostList(queryListResult.getContent(),
				true, false, false);
		return PostResponse.builder().count(queryListResult.getTotalElements()).posts(resultList).build();
	}

	/**
	 * Метод editPost.
	 * Редактирование поста.
	 *
	 * @param editPostRequest - тело запроса.
	 * @param postId          - id поста.
	 * @return - ResponseEntity<?>.
	 */
	public ResponseEntity<?> editPost(PostRequest editPostRequest, long postId, BindingResult bindingResult)
			throws Exception {
		if (bindingResult.hasErrors()) {
			return createBindingErrorResponse(bindingResult, HttpStatus.OK);
		}
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
				messages.getMessage("warning.post.not-found")));
		post.setActive(editPostRequest.isActive());
		post.setTitle(editPostRequest.getTitle());
		post.setText(editPostRequest.getText());
		post.setTime(this.preparePostCreationTime(editPostRequest.getTimestamp()));
		post.setTags(tagService.mapTags(editPostRequest.getTags()));
		post.setModerationStatus(this.acceptModerationStatus());
        this.savePost(post);
		return ResponseEntity.ok(Response.builder().result(true).build());
	}

	/**
	 * Метод moderatePost.
	 * Модерация поста.
	 *
	 * @param moderationRequest - тело запроса.
	 * @param principal         - авторизованный пользователь.
	 * @return Response.
	 */
	public Response moderatePost(ModerationRequest moderationRequest, Principal principal)
			throws PostNotFoundException {
		User activeUser = userService.findByEmail(principal.getName());
		Post post = this.findById(moderationRequest.getPostId());
		if (moderationRequest.getDecision().equals("accept")) {
			post.setModerationStatus(ModerationStatus.ACCEPTED.toString());
		} else {
			post.setModerationStatus(ModerationStatus.DECLINED.toString());
		}
		post.setModeratedBy(activeUser);
		postRepository.save(post);
		this.sendModerationResult(post);
		return Response.builder().result(true).build();
	}

	/**
	 * Метод sendModerationResult.
	 * Отправка итогов модерации автору поста.
	 * @param post пост
	 */
	private void sendModerationResult(Post post) {
		if (post.getModerationStatus().equalsIgnoreCase("accepted")) {
			emailService.send(post.getUser().getEmail(),
					messages.getMessage("post.moderation-accepted-mail-subject"),
					messages.getMessage("post.moderation-accepted-mail-text",
							post.getUser().getName(), post.getTitle())
			);
		} else {
			emailService.send(post.getUser().getEmail(),
					messages.getMessage("post.moderation-declined-mail-subject"),
					messages.getMessage("post.moderation-declined-mail-text",
							post.getUser().getName(), post.getTitle())
			);
		}
	}

	/**
	 * Метод findMyActivePosts.
	 * Поиск активных постов пользователя.
	 *
	 * @param user - пользователь.
	 * @return
	 */
	public List<Post> findMyActivePosts(User user) {
		List<Post> result = postRepository.findByUserAndActiveTrue(user);
		return result != null && !result.isEmpty() ? result : new ArrayList<>();
	}

	/**
	 * Метод findAll.
	 * Поиск/вывод всех постов.
	 *
	 * @return - коллекция, найденных постов.
	 */
	public List<Post> findAll() {
		List<Post> result = new ArrayList<>();
		postRepository.findAll().forEach(result::add);
		return result;
	}

	/**
	 * Метод createCalendarData.
	 * Метод выводит количества публикаций на каждую дату переданного в параметре year года или текущего года,
	 * если параметр year не задан. В параметре years всегда возвращается список всех годов, з
	 * а которые была хотя бы одна публикация, в порядке возрастания.
	 *
	 * @param year - год.
	 * @return CalendarResponse.
	 */
	public CalendarResponse createCalendarData(String year) {
		String queryYear = Strings.isNotEmpty(year) ? year : String.valueOf(LocalDateTime.now().getYear());
		List<String> years = postRepository.findAllYearsWithPosts();
		Map<String, Long> resultMap = postRepository.agregatePostsByYear(Integer.parseInt(queryYear)).stream()
				.collect(Collectors.toMap(t -> t.get(0, String.class), t -> t.get(1, Long.class)));
		return CalendarResponse.builder().years(years).posts(resultMap).build();
	}

	/**
	 * Метод findPostsByStatus.
	 * Вывод количества постов по статусу.
	 *
	 * @param moderationStatus - статус модерации.
	 * @return -
	 */
	public long findPostsByStatus(ModerationStatus moderationStatus) {
		List<Post> newPosts = postRepository
				.findByModerationStatusAndActiveTrue(moderationStatus.name(), null).getContent();
		return !newPosts.isEmpty() ? newPosts.size() : 0;
	}

	/**
	 * Метод findById.
	 * Поиск поста по id.
	 *
	 * @param postId - id искомого поста.
	 * @return - найденный пост.
	 * @throws PostNotFoundException - исключение, если пост не найден.
	 */
	public Post findById(long postId) throws PostNotFoundException {
		return postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(messages.getMessage("warning.post.not-found")));
	}
}
