package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * Класс UserDto
 * Используется для сериализации данных пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see PostResponse
 * @see PostDto
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private long id;
    private String name;
    private String photo;
    private String email;
    private Boolean moderation;
    private Integer moderationCount;
    private Boolean settings;
}
