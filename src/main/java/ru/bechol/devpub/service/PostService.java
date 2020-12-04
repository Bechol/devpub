package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.event.DevpubAppEvent;
import ru.bechol.devpub.models.GlobalSetting;
import ru.bechol.devpub.service.aspect.Trace;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.Role;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.request.ModerationRequest;
import ru.bechol.devpub.request.PostRequest;
import ru.bechol.devpub.response.CalendarResponse;
import ru.bechol.devpub.response.PostDto;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.enums.PostStatus;
import ru.bechol.devpub.service.enums.SortMode;
import ru.bechol.devpub.service.exception.CodeNotFoundException;
import ru.bechol.devpub.service.exception.PostNotFoundException;
import ru.bechol.devpub.service.helper.PostMapperHelper;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
@Trace
public class PostService {

    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    @Value("${time-offset}")
    private String clientZoneOffsetId;
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
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * Метод preparePostCreationTime
     * Проверяет время публикации поста. В случае, если время публикации раньше текущего времени,
     * оно автоматически становиться текущим. Если позже текущего - устанавливается введенное значение.
     *
     * @param timestamp - введеное время публикации поста.
     * @return LocalDateTime.
     */
    private LocalDateTime preparePostCreationTime(long timestamp) {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of(clientZoneOffsetId));
        return timestamp < now ? LocalDateTime.now() :
                LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    /**
     * Метод findAllPostsSorted
     * Метод находити сортирует все посты, в соответствии с заданым режимом mode.
     *
     * @param offset   - сдвиг от 0 для постраничного вывода.
     * @param limit    - количество постов, которое надо вывести.
     * @param sortMode -  режим вывода (сортировка).
     * @return - ResponseEntity<PostsResponse>.
     */
    public ResponseEntity<PostResponse> findAllPostsSorted(int offset, int limit, SortMode sortMode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postPages = postRepository.findByModerationStatusAndActiveTrueAndTimeBefore(
                "ACCEPTED", LocalDateTime.now(), pageable);
        List<PostDto> postDtoList = postMapperHelper.mapPostList(postPages.getContent(),
                true, false, false);
        switch (sortMode) {
            case POPULAR:
                postDtoList.sort(Comparator.comparingLong(PostDto::getCommentCount).reversed());
                break;
            case BEST:
                postDtoList.sort(Comparator.comparingLong(PostDto::getLikeCount).reversed());
                break;
            case EARLY:
                postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp));
                break;
            default:
                postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp).reversed());
        }
        return ResponseEntity.ok(PostResponse.builder().count(postPages.getTotalElements()).posts(postDtoList).build());
    }

    /**
     * Метод findPostsByTextContainingQuery.
     * Метод находит все посты, текст которых содержит строку query.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param query  - поисковый запрос.
     * @return ResponseEntity<PostsResponse>
     */
    public ResponseEntity<PostResponse> findPostsByTextContainingQuery(int offset, int limit, String query) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postPages = postRepository
                .findByModerationStatusAndActiveTrueAndTimeBeforeAndTextContainingIgnoreCase(
                        "ACCEPTED", LocalDateTime.now(), query, pageable);
        List<PostDto> postDtoList = postMapperHelper.mapPostList(
                postPages.getContent(), true, false, false
        );
        return ResponseEntity.ok(PostResponse.builder().count(postPages.getTotalElements()).posts(postDtoList).build());
    }

    /**
     * Метод findPostsByDate.
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param date   - дата, за которую необходимо отобрать посты.
     * @return - фResponseEntity<PostsResponse>.
     */
    public ResponseEntity<PostResponse> findPostsByDate(int offset, int limit, String date) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postPages = postRepository.findByDate(pageable, date);
        List<PostDto> postDtoList = postMapperHelper.mapPostList(
                postPages.getContent(), true, false, false
        );
        return ResponseEntity.ok(PostResponse.builder().count(postPages.getTotalElements()).posts(postDtoList).build());
    }

    /**
     * Метод findByTag.
     * Метод выводит список постов, привязанных к тегу, который был передан методу в качестве параметра tag.
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param tag    - тег, к которому привязаны посты.
     * @return - ResponseEntity<PostsResponse>.
     */
    public ResponseEntity<PostResponse> findByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postPages = postRepository.findByTag(pageable, tag);
        List<PostDto> postDtoList = postMapperHelper.mapPostList(
                postPages.getContent(), true, false, false
        );
        return ResponseEntity.ok(PostResponse.builder().count(postPages.getTotalElements()).posts(postDtoList).build());
    }

    /**
     * Метод findActiveUserPosts.
     * Метод формирует ответ на GET запрос /api/post/my.
     *
     * @param offset     - сдвиг от 0 для постраничного вывода
     * @param limit      - количество постов, которое надо вывести
     * @param postStatus - статус модерации.
     * @return - ResponseEntity<PostsResponse>.
     */
    public PostResponse findActiveUserPosts(Principal principal, int offset, int limit, PostStatus postStatus) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.ASC, "time");
        List<Post> myPostList = userService.findByEmail(principal.getName()).getPosts();
        switch (postStatus) {
            case PENDING:
                myPostList = this.filterActivePostByModerationStatus(myPostList, "NEW");
                break;
            case DECLINED:
                myPostList = this.filterActivePostByModerationStatus(myPostList, "DECLINED");
                break;
            case PUBLISHED:
                myPostList = this.filterActivePostByModerationStatus(myPostList, "ACCEPTED");
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
    private List<Post> filterActivePostByModerationStatus(List<Post> postList, String moderationStatus) {
        return postList.stream().filter(post ->
                post.isActive() && post.getModerationStatus().equals(moderationStatus)).collect(Collectors.toList()
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
            throws RoleNotFoundException, CodeNotFoundException {
        if (bindingResult.hasErrors()) {
            return createBindingErrorResponse(bindingResult, HttpStatus.OK);
        }
        Role roleModerator = roleService.findByName("ROLE_MODERATOR");
        Post newPost = new Post();
        newPost.setTime(this.preparePostCreationTime(postRequest.getTimestamp()));
        newPost.setActive(postRequest.isActive());
        newPost.setTitle(postRequest.getTitle());
        newPost.setText(postRequest.getText());
        newPost.setModerationStatus(this.acceptModerationStatus());
        newPost.setUser(userService.findByEmail(principal.getName()));
        newPost.setModerator(roleModerator.getUsers().stream().findFirst().orElse(null));
        if (postRequest.getTags().size() > 0) {
            newPost.setTags(tagService.mapTags(postRequest.getTags()));
        }
        applicationEventPublisher.publishEvent(new DevpubAppEvent<>(
                this, newPost, DevpubAppEvent.EventType.SAVE_POST
        ));
        return ResponseEntity.ok(Response.builder().result(true).build());
    }

    /**
     * Метод acceptModerationStatus.
     * Выбор статуса модерации в зависимости от настройки премодерации.
     *
     * @return ModerationStatus.NEW - если установлен режим премодерации.
     * @throws CodeNotFoundException - если настрока не найдена по коду.
     */
    private String acceptModerationStatus() throws CodeNotFoundException {
        if (globalSettingsService.checkSetting("POST_PREMODERATION", "YES")) {
            return "NEW";
        } else {
            return "ACCEPTED";
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
    public ResponseEntity<PostDto> showPost(long postId, Principal principal) throws PostNotFoundException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
                messages.getMessage("warning.not-found", "post")));
        User activeUser = null;
        if (principal != null) {
            activeUser = userService.findByEmail(principal.getName());
        } else {
            post = increaseViewCount(post);
            return ResponseEntity.ok().body(postMapperHelper.mapPost(post,
                    false, true, true));
        }
        boolean isModerator = activeUser.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_MODERATOR));
        boolean isAuthor = post.getUser().getId() == activeUser.getId();
        if (isAuthor || isModerator) {
            return ResponseEntity.ok(postMapperHelper.mapPost(
                    post, false, true, true));
        }
        post = increaseViewCount(post);
        return ResponseEntity.ok(postMapperHelper.mapPost(
                post, false, true, true));
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
     * @param principal        - авторизованный пользователь.
     * @param offset           - сдвиг от 0 для постраничного вывода
     * @param limit            - количество постов, которое надо вывести
     * @param moderationStatus - статус модерации.
     * @return PostResponse.
     */
    public PostResponse findPostsOnModeration(Principal principal, int offset, int limit, String moderationStatus) {
        User moderator = userService.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.ASC, "time");
        Page<Post> queryListResult;
        switch (moderationStatus) {
            case "ACCEPTED":
                queryListResult = postRepository.findByModeratedByAndModerationStatusAndActiveTrue(moderator,
                        "ACCEPTED", pageable);
                break;
            case "DECLINED":
                queryListResult = postRepository.findByModeratedByAndModerationStatusAndActiveTrue(moderator,
                        "DECLINED", pageable);
                break;
            default:
                queryListResult = postRepository.
                        findByModerationStatusAndActiveTrue("NEW", pageable);
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
     * @param principal       - авторизованный пользователь.
     * @return - ResponseEntity<?>.
     */
    public ResponseEntity<?> editPost(PostRequest editPostRequest, long postId, Principal principal,
                                      BindingResult bindingResult) throws PostNotFoundException {
        if (bindingResult.hasErrors()) {
            return createBindingErrorResponse(bindingResult, HttpStatus.OK);
        }
        User activeUser = userService.findByEmail(principal.getName());
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(
                messages.getMessage("warning.not-found", "post")));
        post.setActive(editPostRequest.isActive());
        post.setTitle(editPostRequest.getTitle());
        post.setText(editPostRequest.getText());
        post.setTime(this.preparePostCreationTime(editPostRequest.getTimestamp()));
        post.setTags(tagService.mapTags(editPostRequest.getTags()));
        if (!activeUser.isModerator()) {
            post.setModerationStatus("NEW");
        }
        postRepository.save(post);
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
            post.setModerationStatus("ACCEPTED");
        } else {
            post.setModerationStatus("DECLINED");
        }
        post.setModeratedBy(activeUser);
        postRepository.save(post);
        return Response.builder().result(true).build();
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
    public long findPostsByStatus(String moderationStatus) {
        List<Post> newPosts = postRepository
                .findByModerationStatusAndActiveTrue(moderationStatus, null).getContent();
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
        return postRepository.findById(postId).
                orElseThrow(() -> new PostNotFoundException(
                        messages.getMessage("warning.not-found", "post")
                ));
    }
}
