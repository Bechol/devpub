package ru.bechol.devpub.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс ModerationRequest.
 * Для десеарилизации тела POST запроса /api/moderation
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.ModerationController
 * @see ru.bechol.devpub.service.IPostService
 */
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModerationRequest {

	Long post_id;
	@Getter
	String decision;

	public Long getPostId() {
		return post_id;
	}
}
