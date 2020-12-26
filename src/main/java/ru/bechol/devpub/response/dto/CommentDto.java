package ru.bechol.devpub.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.service.impl.CommentService;

/**
 * Класс CommentDto.
 * Сериализация коментария.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CommentService
 * @see ru.bechol.devpub.models.Comment
 * @see PostDto
 * @see PostResponse
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {

	long id;
	long timestamp;
	String text;
	UserDto user;
}
