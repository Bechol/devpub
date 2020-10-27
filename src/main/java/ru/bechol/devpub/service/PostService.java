package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.response.PostsResponse;

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

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private Messages messages;

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
     * Форматирование коллекции постов, полученной
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
                .timestamp(post.getTime().toEpochSecond(ZoneOffset.UTC))
                .title(post.getTitle())
                .announce(post.getText().substring(0, 100))
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .user(post.getUser())
                .build()).collect(Collectors.toList());
    }


}
