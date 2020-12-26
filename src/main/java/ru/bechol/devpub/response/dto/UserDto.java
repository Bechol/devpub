package ru.bechol.devpub.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bechol.devpub.response.PostResponse;

/**
 * Класс UserDto
 * Используется для сериализации данных пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.IUserService
 * @see PostResponse
 * @see PostDto
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

	long id;
	String name;
	String photo;
	String email;
	Boolean moderation;
	Long moderationCount;
	Boolean settings;

}
