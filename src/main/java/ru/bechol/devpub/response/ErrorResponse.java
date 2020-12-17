package ru.bechol.devpub.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс ErrorResponse.
 * Ответ с кодом 400 (Bad request) и сообщением (объект с ключом "message").
 * На frontend будет выводиться alert с текстом сообщения, переданным в ответе сервера в параметре "message".
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {

	String message;
}
