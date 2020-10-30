package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * Класс CommentDto.
 * Сериализация данных коментария
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.Comment
 * @see PostDto
 * @see PostResponse
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {

    private long id;
    private long timestamp;
    private String text;
    private UserDto user;
}
