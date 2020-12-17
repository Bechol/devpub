package ru.bechol.devpub.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

/**
 * Класс CommentRequest.
 * Для десеарилизации тела запроса на создание комментария к посту.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.CommentController
 * @see ru.bechol.devpub.service.CommentService
 */
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {

	String parent_id;
	String post_id;
	@Getter
	@Size(min = 20, max = 500, message = "Текст комментария может быть размером от 20-ти до 500-а символов.")
	String text;

	public String getParentId() {
		return parent_id;
	}

	public String getPostId() {
		return post_id;
	}
}
