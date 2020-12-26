package ru.bechol.devpub.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Класс PostRequest.
 * Для десеарилизации тела запросов создания и изменения поста.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.PostController
 * @see ru.bechol.devpub.service.IPostService
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {

	long timestamp;
	boolean active;
	@Size(min = 3, max = 100, message = "Длина заголовка должна быть не менее 3-х и не более 100-а символов.")
	String title;
	List<String> tags;
	@Size(min = 50, max = 20000, message = "Длина текста должна быть не менее 50-ти и не более 20000 символов.")
	String text;
}
