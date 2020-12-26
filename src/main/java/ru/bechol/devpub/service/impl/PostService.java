package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.repository.IPostRepository;
import ru.bechol.devpub.request.*;
import ru.bechol.devpub.response.*;
import ru.bechol.devpub.response.dto.PostDto;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.enums.*;
import ru.bechol.devpub.service.exception.*;
import ru.bechol.devpub.service.helper.*;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class PostService implements IPostService {

	private static final String ROLE_MODERATOR = "ROLE_MODERATOR";

	@Autowired
	IPostRepository postRepository;
	@Autowired
	@Qualifier("userService")
	private IUserService userService;
	@Autowired
	@Qualifier("roleService")
	private IRoleService roleService;
	@Autowired
	@Qualifier("tagService")
	ITagService tagService;
	@Autowired
	@Qualifier("globalSettingsService")
	IGlobalSettingsService globalSettingsService;
	@Autowired
	@Qualifier("emailService")
	IEmailService emailService;
	@Autowired
	Messages messages;
	@Autowired
	PostMapperHelper postMapperHelper;
	@Autowired
	ModeratorLoadBalancer moderatorLoadBalancer;


	/**
	 * Метод getPostsWithSortMode.
	 * Формирует список постов для отображения в зависимости от заданного режима сортировки.
	 *
	 * @param offset   сдвиг от 0 для постраничного вывода.
	 * @param limit    количество постов, которое надо вывести.
	 * @param sortMode режим сортировки.
	 * @return PostResponse
	 */
	@Override
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
	 * Метод findPostsByTextContainingQuery.
	 * Метод находит все посты, текст которых содержит строку query.
	 *
	 * @param offset сдвиг от 0 для постраничного вывода.
	 * @param limit  количество постов, которое надо вывести.
	 * @param query  поисковый запрос.
	 * @return PostResponse
	 */
	@Override
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
	 * @param offset сдвиг от 0 для постраничного вывода
	 * @param limit  количество постов, которое надо вывести
	 * @param date   дата, за которую необходимо отобрать посты.
	 * @return - PostResponse.
	 */
	@Override
	public PostResponse findPostsByDate(int offset, int limit, String date) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Post> postPages = postRepository.findByDate(pageable, date);
		return this.createPostResponse(postPages, true, false, false);
	}

	/**
	 * Метод findByTag.
	 * Метод выводит список постов, привязанных к тегу, который был передан методу в качестве параметра tag.
	 *
	 * @param offset сдвиг от 0 для постраничного вывода
	 * @param limit  количество постов, которое надо вывести
	 * @param tag    тег, к которому привязаны посты.
	 * @return - PostResponse.
	 */
	@Override
	public PostResponse findByTag(int offset, int limit, String tag) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<Post> postPages = postRepository.findByTag(pageable, tag);
		return this.createPostResponse(postPages, true, false, false);
	}

	/**
	 * Метод findActiveUserPosts.
	 * Метод формирует ответ на GET запрос /api/post/my.
	 *
	 * @param offset     сдвиг от 0 для постраничного вывода
	 * @param limit      количество постов, которое надо вывести
	 * @param postStatus статус модерации.
	 * @return - PostResponse.
	 */
	@Override
	public PostResponse findActiveUserPosts(User user, int offset, int limit, PostStatus postStatus) {
		Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.ASC, "time");
		Page<Post> postsByQuery = null;
		switch (postStatus) {
			case PENDING:
				postsByQuery = postRepository.findByUserAndActiveAndModerationStatus(
						user, true, ModerationStatus.NEW.toString(), pageable);
				break;
			case DECLINED:
				postsByQuery = postRepository.findByUserAndActiveAndModerationStatus(
						user, true, ModerationStatus.DECLINED.toString(), pageable);
				break;
			case PUBLISHED:
				postsByQuery = postRepository.findByUserAndActiveAndModerationStatus(
						user, true, ModerationStatus.ACCEPTED.toString(), pageable);
				break;
			case INACTIVE:
				postsByQuery = postRepository.findByUserAndActiveFalse(user, pageable);
				break;
		}
		return this.createPostResponse(postsByQuery, true, false, false);
	}

	/**
	 * Метод createNewPost.
	 * Создание поста.
	 *
	 * @param user          авторизованный пользователь.
	 * @param postRequest   данные нового поста.
	 * @param bindingResult результаты валидации данных нового поста.
	 * @return - ResponseEntity<Response<?>>.
	 */
	@Override
	public ResponseEntity<?> createNewPost(User user, PostRequest postRequest, BindingResult bindingResult)
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
		newPost.setUser(user);
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
	 *
	 * @param post пост, который необходимо сохранить,
	 */
	@Override
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
	 * Метод showPost.
	 * Формирование ответа на запрос GET /api/post/{$ID}
	 *
	 * @param postId id поста.
	 * @param user   авторизованный пользователь.
	 * @return ResponseEntity<PostDto>
	 */
	@Override
	public PostDto showPost(long postId, User user) throws PostNotFoundException {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
				messages.getMessage("warning.post.not-found")));
		if (Objects.isNull(user)) {
			post = increaseViewCount(post);
			return postMapperHelper.mapPost(post, false, true, true);
		} else if (post.getUser().getId() == user.getId() || user.isModerator()) {
			return postMapperHelper.mapPost(post, false, true, true);
		}
		post = increaseViewCount(post);
		return postMapperHelper.mapPost(post, false, true, true);
	}

	/**
	 * Метод editPost.
	 * Редактирование поста.
	 *
	 * @param editPostRequest тело запроса.
	 * @param postId          id поста.
	 * @return ResponseEntity<?>.
	 */
	@Override
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
	 * Метод acceptModerationStatus.
	 * Выбор статуса модерации в зависимости от настройки премодерации.
	 *
	 * @return ModerationStatus.NEW если установлен режим премодерации.
	 * @throws CodeNotFoundException сли настрока не найдена по коду.
	 */
	private String acceptModerationStatus() throws CodeNotFoundException {
		if (globalSettingsService.checkSetting("POST_PREMODERATION", SettingValue.YES)) {
			return ModerationStatus.NEW.toString();
		} else {
			return ModerationStatus.ACCEPTED.toString();
		}
	}

	/**
	 * Метод increaseViewCount.
	 * Увеличение количества просмотров поста.
	 *
	 * @param post пост, в котором увеличиваем количество просмотров.
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
	 * @param user   авторизованный пользователь.
	 * @param offset сдвиг от 0 для постраничного вывода
	 * @param limit  количество постов, которое надо вывести
	 * @param status статус модерации.
	 * @return PostResponse.
	 */
	@Override
	public PostResponse findPostsOnModeration(User user, int offset, int limit, String status)
			throws EnumValueNotFoundException {
		Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.ASC, "time");
		Page<Post> queryListResult;
		switch (ModerationStatus.fromValue(status)) {
			case ACCEPTED:
				queryListResult = postRepository.findByModeratedByAndModerationStatusAndActiveTrue(user,
						ModerationStatus.ACCEPTED.toString(), pageable);
				break;
			case DECLINED:
				queryListResult = postRepository.findByModeratedByAndModerationStatusAndActiveTrue(user,
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
	 * Метод moderatePost.
	 * Модерация поста.
	 *
	 * @param moderationRequest тело запроса.
	 * @param user              авторизованный пользователь.
	 * @return Response.
	 */
	@Override
	public Response moderatePost(ModerationRequest moderationRequest, User user)
			throws PostNotFoundException {
		Post post = this.findById(moderationRequest.getPostId());
		if (moderationRequest.getDecision().equals("accept")) {
			post.setModerationStatus(ModerationStatus.ACCEPTED.toString());
		} else {
			post.setModerationStatus(ModerationStatus.DECLINED.toString());
		}
		post.setModeratedBy(user);
		postRepository.save(post);
		this.sendModerationResultEmail(post);
		return Response.builder().result(true).build();
	}

	/**
	 * Метод sendModerationResult.
	 * Отправка итогов модерации автору поста.
	 *
	 * @param post пост
	 */
	private void sendModerationResultEmail(Post post) {
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
	 * Метод findAll.
	 * Поиск/вывод всех постов.
	 *
	 * @return коллекция, найденных постов.
	 */
	@Override
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
	 * @param year год.
	 * @return CalendarResponse.
	 */
	@Override
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
	 * @param moderationStatus статус модерации.
	 * @return количество постов.
	 */
	@Override
	public long findPostsByStatus(ModerationStatus moderationStatus) {
		List<Post> newPosts = postRepository
				.findByModerationStatusAndActiveTrue(moderationStatus.name(), null).getContent();
		return !newPosts.isEmpty() ? newPosts.size() : 0;
	}

	/**
	 * Метод findById.
	 * Поиск поста по id.
	 *
	 * @param postId id искомого поста.
	 * @return найденный пост.
	 * @throws PostNotFoundException - исключение, если пост не найден.
	 */
	@Override
	public Post findById(long postId) throws PostNotFoundException {
		return postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(messages.getMessage("warning.post.not-found")));
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
}
