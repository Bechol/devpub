package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.Role;
import ru.bechol.devpub.models.Tag;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.repository.RoleRepository;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.request.ModerationRequest;
import ru.bechol.devpub.request.PostRequest;
import ru.bechol.devpub.response.*;

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
public class PostService { //todo рефакторинг

    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    @Value("${time-offset}")
    private String clientZoneOffsetId;
    @Value("${announce.string.length}")
    private int announceStringLength;
    @Value("${announce.string.end}")
    private String announceStringEnd;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private Messages messages;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Метод createNewPost.
     * Создание поста.
     *
     * @param principal     - авторизованный пользователь.
     * @param postRequest   - данные нового поста.
     * @param bindingResult - результаты валидации данных нового поста.
     * @return - ResponseEntity<Response<?>>.
     */
    public ResponseEntity<Response<?>> createNewPost(Principal principal, PostRequest postRequest,
                                                     BindingResult bindingResult) {
        User activeUser = userService.findByEmail(principal.getName()).orElse(null);
        if (activeUser == null) {
            bindingResult.addError(new FieldError(
                    "user", "user", messages.getMessage("user.not-found.by-email")));
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.ok(Response.builder().result(false).errors(errorMap).build());
        }
        Role role = roleRepository.findByName("ROLE_MODERATOR").orElse(null);
        Post newPost = new Post();
        newPost.setTime(this.preparePostCreationTime(postRequest.getTimestamp()));
        newPost.setActive(postRequest.isActive());
        newPost.setTitle(postRequest.getTitle());
        newPost.setText(postRequest.getText());
        newPost.setModerationStatus(Post.ModerationStatus.NEW);
        newPost.setUser(activeUser);
        newPost.setModerator(role.getUsers().stream().findFirst().orElse(null));
        if (postRequest.getTags().size() > 0) {
            newPost.setTags(tagService.mapTags(postRequest.getTags()));
        }
        postRepository.save(newPost);
        return ResponseEntity.ok(Response.builder().result(true).build());
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
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of(clientZoneOffsetId));
        return timestamp < now ? LocalDateTime.now() :
                LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    /**
     * Метод findAllSorted
     * Метод находити сортирует все посты, в соответствии с заданым режимом mode.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param mode   -  режим вывода (сортировка).
     * @return - ResponseEntity<PostsResponse>.
     */
    public PostResponse findAllSorted(int offset, int limit, String mode) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postListFromQuery = postRepository.findAllPostsUnsorted(pageable).getContent();
        List<PostDto> postDtoList = mapPostList(postListFromQuery, true, false, false);
        switch (mode) {
            case "recent":
                postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp).reversed());
                break;
            case "popular":
                postDtoList.sort(Comparator.comparingLong(PostDto::getCommentCount).reversed());
                break;
            case "best":
                postDtoList.sort(Comparator.comparingLong(PostDto::getLikeCount).reversed());
                break;
            case "early":
                postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp));
                break;
            default:
                log.info(messages.getMessage("post.sort-mode.not-defined", mode));
        }
        return PostResponse.builder().count(postDtoList.size()).posts(postDtoList).build();
    }

    /**
     * Метод findByQuery.
     * Метод находит все посты, текст которых содержит строку query.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param query  - поисковый запрос.
     * @return ResponseEntity<PostsResponse>
     */
    public PostResponse findByQuery(int offset, int limit, String query) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postListFromQuery = postRepository.findAllPostsByQuery(pageable, query);
        List<PostDto> postDtoList = mapPostList(postListFromQuery.getContent(), true, false, false);
        return PostResponse.builder().count(postDtoList.size()).posts(postDtoList).build();
    }

    /**
     * Метод mapPostList.
     * Формирование коллекции PostDto для ответа сервера.
     *
     * @param postsByQueryList - коллекция постов
     * @param includeAnnounce  - флаг для включения в ответ краткого содержания поста.
     * @param includeComments  - флаг для включения в ответ сервера комментариев поста.
     * @param includeTags      - флаг для включения в ответ сервера тегов.
     * @return - List<PostDto>
     */
    private List<PostDto> mapPostList(List<Post> postsByQueryList, boolean includeAnnounce,
                                      boolean includeComments, boolean includeTags) {
        return postsByQueryList.stream().map(post -> mapPost(post, includeAnnounce, includeComments, includeTags))
                .collect(Collectors.toList());
    }

    /**
     * Метод mapPost.
     * Создание объекта PostDto.
     *
     * @param post            - пост.
     * @param includeAnnounce - флаг для включения в ответ краткого содержания поста.
     * @param includeComments - флаг для включения в ответ сервера комментариев поста.
     * @param includeTags     - флаг для включения в ответ сервера тегов.
     * @return - PostDto
     */
    private PostDto mapPost(Post post, boolean includeAnnounce, boolean includeComments, boolean includeTags) {
        return PostDto.builder()
                .id(post.getId())
                .active(post.isActive())
                .timestamp(post.getTime().toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond())
                .user(UserDto.builder().id(post.getUser().getId()).name(post.getUser().getName()).build())
                .title(post.getTitle())
                .text(post.getText())
                .announce(includeAnnounce ? post.getText().substring(0, announceStringLength)
                        .concat(announceStringEnd) : null)
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .comments(includeComments ? mapPostCommentList(post) : null)
                .tags(includeTags ? mapPostTags(post) : null)
                .build();
    }

    /**
     * Метод mapPostCommentList.
     * Фформирование списка комментариев к посту.
     *
     * @param post - пост.
     * @return List<CommentDto>.
     */
    private List<CommentDto> mapPostCommentList(Post post) {
        return post.getComments().stream().map(comment -> CommentDto.builder()
                .id(comment.getId())
                .timestamp(comment.getTime().toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond())
                .text(comment.getText())
                .user(UserDto.builder().
                        id(post.getUser().getId())
                        .name(post.getUser().getName())
                        .photo(post.getUser().getPhotoLink())
                        .build())
                .build())
                .collect(Collectors.toList());
    }

    /**
     * Метод mapPostTags.
     * Преобразование имен тегов поста в список.
     *
     * @param post - пост.
     * @return List<String>.
     */
    private List<String> mapPostTags(Post post) {
        return post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    }

    /**
     * Метод findByDate.
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param date   - дата, за которую необходимо отобрать посты.
     * @return - фResponseEntity<PostsResponse>.
     */
    public PostResponse findByDate(int offset, int limit, String date) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postListFromQuery = postRepository.findAllPostsByDate(pageable, date).getContent();
        List<PostDto> postDtoList = mapPostList(postListFromQuery, true, false, false);
        return PostResponse.builder().count(postDtoList.size()).posts(postDtoList).build();
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
    public PostResponse findByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postListFromQuery = postRepository.findAllByTag(pageable, tag).getContent();
        List<PostDto> postDtoList = mapPostList(postListFromQuery, true, false, false);
        return PostResponse.builder().count(postDtoList.size()).posts(postDtoList).build();
    }

    /**
     * Метод findMyPosts.
     * Метод формирует ответ GET запрос /api/post/my.
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @param status - статус модерации.
     * @return - ResponseEntity<PostsResponse>.
     */

    public PostResponse findMyPosts(Principal principal, int offset, int limit, String status) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        List<Post> postList = user.getPosts();
        switch (status) {
            case "inactive":
                postList = postList.stream().filter(post -> !post.isActive()).collect(Collectors.toList());
                break;
            case "pending":
                postList = postList.stream().filter(post ->
                        post.isActive() && post.getModerationStatus().equals(Post.ModerationStatus.NEW))
                        .collect(Collectors.toList());
                break;
            case "declined":
                postList = postList.stream().filter(post ->
                        post.isActive() && post.getModerationStatus().equals(Post.ModerationStatus.DECLINED))
                        .collect(Collectors.toList());
                break;
            case "published":
                postList = postList.stream().filter(post ->
                        post.isActive() && post.getModerationStatus().equals(Post.ModerationStatus.ACCEPTED))
                        .collect(Collectors.toList());
                break;
            default:
                log.info("User don't have posts");
        }
        List<PostDto> resultList = mapPostList(postList, true, false, false);
        Page<PostDto> postPages = new PageImpl<>(resultList, pageable, resultList.size());
        return PostResponse.builder().count(resultList.size()).posts(postPages.getContent()).build();
    }

    /**
     * Метод showPost.
     * Формирование ответа на запрос GET /api/post/{$ID}
     *
     * @param postId    - id поста.
     * @param principal - авторизованный пользователь.
     * @return ResponseEntity<PostDto>
     */
    public ResponseEntity<PostDto> showPost(long postId, Principal principal) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        User activeUser = null;
        if (principal != null) {
            activeUser = userRepository.findByEmail(principal.getName()).orElse(null);
        } else {
            post = increaseViewCount(post);
            return ResponseEntity.ok().body(mapPost(post, false, true, true));
        }
        boolean isModerator = activeUser.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_MODERATOR));
        boolean isAuthor = post.getUser().getId() == activeUser.getId();
        if (isAuthor || isModerator) {
            return ResponseEntity.ok(mapPost(post, false, true, true));
        }
        post = increaseViewCount(post);
        return ResponseEntity.ok(mapPost(post, false, true, true));
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
    public PostResponse findPostsOnModeration(Principal principal, int offset, int limit, String status) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        User moderator = userService.findByEmail(principal.getName()).orElse(null);
        if (moderator == null) {
            return PostResponse.builder().count(0).posts(new ArrayList<>()).build();
        }
        List<PostDto> resultList = new ArrayList<>();
        switch (status) {
            case "new": //все активные посты со статусом "НОВЫЙ"
                resultList = mapPostList(postRepository.findByModerationStatusAndActiveTrue(Post.ModerationStatus.NEW),
                        true, false, false);
                break;
            case "accepted": //все активные, утвержденные мной посты
                resultList = mapPostList(postRepository
                                .findByModeratedByAndModerationStatusAndActiveTrue(moderator,
                                        Post.ModerationStatus.ACCEPTED),
                        true, false, false);
                break;
            case "declined": //все активные, отклоненные мной посты
                resultList = mapPostList(postRepository.findByModeratedByAndModerationStatusAndActiveTrue(moderator,
                        Post.ModerationStatus.DECLINED),
                        true, false, false);
                break;
            default:
                log.info(messages.getMessage("post.sort-mode.not-defined"));
        }
        Page<PostDto> postPages = new PageImpl<>(resultList, pageable, resultList.size());
        return PostResponse.builder().count(resultList.size()).posts(postPages.getContent()).build();
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
    public ResponseEntity<?> editPost(PostRequest editPostRequest, long postId, Principal principal) {
        User activeUser = userService.findActiveUser(principal);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || activeUser == null) {
            return ResponseEntity.ok(Response.builder().result(false).build());
        }
        post.setActive(editPostRequest.isActive());
        post.setTitle(editPostRequest.getTitle());
        post.setText(editPostRequest.getText());
        post.setTime(this.preparePostCreationTime(editPostRequest.getTimestamp()));
        post.setTags(tagService.mapTags(editPostRequest.getTags()));
        if (!activeUser.isModerator()) {
            post.setModerationStatus(Post.ModerationStatus.NEW);
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
    public Response moderatePost(ModerationRequest moderationRequest, Principal principal) {
        User activeUser = userService.findActiveUser(principal);
        if (activeUser == null || !activeUser.isModerator()) {
            return Response.builder().result(false).build();
        }
        Post post = postRepository.findById(moderationRequest.getPostId()).orElse(null);
        if (post == null) {
            return Response.builder().result(false).build();
        }
        if (moderationRequest.getDecision().equals("accept")) {
            post.setModerationStatus(Post.ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(Post.ModerationStatus.DECLINED);
        }
        post.setModeratedBy(activeUser);
        postRepository.save(post);
        return Response.builder().result(true).build();
    }


    public List<Post> findMyActivePosts(User user) {
        List<Post> result = postRepository.findByUserAndActiveTrue(user);
        return result != null && !result.isEmpty() ? result : new ArrayList<>();
    }

    public List<Post> findAll() {
        List<Post> result = new ArrayList<>();
        postRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     * Метод createCalendarData.
     * Метод выводит количества публикаций на каждую дату переданного в параметре year года или текущего года,
     * если параметр year не задан. В параметре years всегда возвращается список всех годов, з
     * а которые была хотя бы одна публикация, в порядке возврастания.
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

    public long findPostsByStatus(Post.ModerationStatus moderationStatus) {
        List<Post> newPosts = postRepository.findByModerationStatusAndActiveTrue(moderationStatus);
        return !newPosts.isEmpty() ? newPosts.size() : 0;
    }
}
