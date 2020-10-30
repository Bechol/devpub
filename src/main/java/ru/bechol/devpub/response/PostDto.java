package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Класс PostDto.
 * Используется для сериализации поста.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CommentDto
 * @see UserDto
 * @see ru.bechol.devpub.models.Tag
 * @see ru.bechol.devpub.models.Post
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

    private long id;
    private long timestamp;
    private Boolean active;
    private UserDto user;
    private String title;
    private String announce;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
    private long viewCount;
    private List<CommentDto> comments;
    private List<String> tags;
}
