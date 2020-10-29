package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.response.PostsResponse;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
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
public class PostService {

    @Value("${time-offset}")
    private String clientZoneOffsetId;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private Messages messages;
    @Autowired
    private UserRepository userRepository;

    /**
     * Метод findAllSorted
     * Метод находити сортирует все посты, в соответствии с заданым режимом mode.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param mode   -  режим вывода (сортировка).
     * @return - ResponseEntity<PostsResponse>.
     */
    public ResponseEntity<PostsResponse> findAllSorted(int offset, int limit, String mode) {
        List<PostsResponse.PostBody> resultList = prepareResponseResultList(offset, limit, null);
        switch (mode) {
            case "recent":
                resultList.sort(Comparator.comparingLong(PostsResponse.PostBody::getTimestamp).reversed());
                break;
            case "popular":
                resultList.sort(Comparator.comparingLong(PostsResponse.PostBody::getCommentCount).reversed());
                break;
            case "best":
                resultList.sort(Comparator.comparingLong(PostsResponse.PostBody::getLikeCount).reversed());
                break;
            case "early":
                resultList.sort(Comparator.comparingLong(PostsResponse.PostBody::getTimestamp));
                break;
            default:
                log.info(messages.getMessage("post.sort-mode.not-defined", mode));
        }
        return ResponseEntity.ok().body(PostsResponse.builder().count(resultList.size()).posts(resultList).build());
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
    public ResponseEntity<PostsResponse> findByQuery(int offset, int limit, String query) {
        List<PostsResponse.PostBody> resultList = prepareResponseResultList(offset, limit, query);
        return ResponseEntity.ok().body(PostsResponse.builder().count(resultList.size()).posts(resultList).build());
    }

    /**
     * Метод prepareResponseResultList.
     * Форматирование коллекции постов.
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести
     * @return - форматированный список постов для добавления к ответу сервера.
     */
    private List<PostsResponse.PostBody> prepareResponseResultList(int offset, int limit, String query) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postListFromQuery = Strings.isNotEmpty(query) ?
                postRepository.findAllPostsByQuery(pageable, query).getContent() :
                postRepository.findAllPostsUnsorted(pageable).getContent();
        return postListFromQuery.stream().map(post -> PostsResponse.PostBody.builder()
                .id(post.getId())
                .timestamp(post.getTime().toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond())
                .title(post.getTitle())
                .announce(post.getText().substring(0, 100))
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .user(post.getUser())
                .build()).collect(Collectors.toList());
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
    public ResponseEntity<PostsResponse> findByDate(int offset, int limit, String date) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postListFromQuery = postRepository.findAllPostsByDate(pageable, date).getContent();
        List<PostsResponse.PostBody> resultList = postListFromQuery.stream().map(post -> PostsResponse.PostBody.builder()
                .id(post.getId())
                .timestamp(post.getTime().toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond())
                .title(post.getTitle())
                .announce(post.getText().substring(0, 100))
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .user(post.getUser())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok().body(PostsResponse.builder().count(resultList.size()).posts(resultList).build());
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
    public ResponseEntity<PostsResponse> findByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Post> postListFromQuery = postRepository.findAllByTag(pageable, tag).getContent();
        List<PostsResponse.PostBody> resultList = postListFromQuery.stream().map(post -> PostsResponse.PostBody.builder()
                .id(post.getId())
                .timestamp(post.getTime().toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond())
                .title(post.getTitle())
                .announce(post.getText().substring(0, 100))
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .user(post.getUser())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok().body(PostsResponse.builder().count(resultList.size()).posts(resultList).build());
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

    public ResponseEntity<PostsResponse> findMyPosts(Principal principal, int offset, int limit, String status) {
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
        List<PostsResponse.PostBody> resultList = postList.stream().map(post -> PostsResponse.PostBody.builder()
                .id(post.getId())
                .timestamp(post.getTime().toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond())
                .title(post.getTitle())
                .announce(post.getText().substring(0, 100))
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .user(post.getUser())
                .build()).collect(Collectors.toList());
        List<PostsResponse.PostBody> postPages = new PageImpl<>(resultList, pageable, resultList.size()).getContent();
        return ResponseEntity.ok().body(PostsResponse.builder().count(resultList.size()).posts(postPages).build());
    }
}
