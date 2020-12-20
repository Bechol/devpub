package ru.bechol.devpub.service.helper;

import lombok.Data;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.response.dto.CommentDto;
import ru.bechol.devpub.response.dto.PostDto;
import ru.bechol.devpub.response.dto.UserDto;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс PostMapperHelper.
 * Маппинг постов.
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.PostService
 * @see Post
 * @see PostDto
 * @see CommentDto
 */
@Component
@Data
public class PostMapperHelper {

    @Value("${announce.string.length}")
    private int announceStringLength;
    @Value("${announce.string.end}")
    private String announceStringEnd;

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
    public List<PostDto> mapPostList(List<Post> postsByQueryList, boolean includeAnnounce,
                                     boolean includeComments, boolean includeTags) {
        return postsByQueryList.stream()
                .map(post -> mapPost(post, includeAnnounce, includeComments, includeTags))
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
    public PostDto mapPost(Post post, boolean includeAnnounce, boolean includeComments, boolean includeTags) {
        return PostDto.builder()
                .id(post.getId())
                .active(post.isActive())
                .timestamp(post.getTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                .user(UserDto.builder().id(post.getUser().getId()).name(post.getUser().getName()).build())
                .title(post.getTitle())
                .text(post.getText())
                .announce(includeAnnounce && Jsoup.parse(post.getText() ).text().length() > announceStringLength ?
                        this.createAnnounce(post.getText()) : "")
                .likeCount(post.getVotes().stream().filter(vote -> vote.getValue() == 1).count())
                .dislikeCount(post.getVotes().stream().filter(vote -> vote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .comments(includeComments ? this.mapPostCommentList(post) : null)
                .tags(includeTags ? this.mapPostTags(post) : null)
                .build();
    }

    /**
     * Метод createAnnounce.
     * Создание краткого описания поста.
     * @param postText - текст поста.
     * @return - краткое описание поста.
     */
    private String createAnnounce(String postText) {
        return Jsoup.parse(postText).text().substring(0, this.announceStringLength).concat(this.announceStringEnd);
    }

    /**
     * Метод mapPostCommentList.
     * Формирование списка комментариев к посту.
     *
     * @param post - пост.
     * @return List<CommentDto>.
     */
    private List<CommentDto> mapPostCommentList(Post post) {
        return post.getComments().stream().sorted(Comparator.comparing(Comment::getId))
                .map(comment -> CommentDto.builder()
                    .id(comment.getId())
                    .timestamp(comment.getTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                    .text(comment.getText())
                    .user(UserDto.builder()
                            .id(comment.getUser().getId())
                            .name(comment.getUser().getName())
                            .photo(comment.getUser().getPhotoLink())
                            .build())
                    .build()
                )
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
}
