package ru.bechol.devpub.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс PostIdRequest.
 * Для десеарилизации тела запроса GET /api/post/like
 *
 * @author Oleg Bech
 * @version 1.0
 * @see ru.bechol.devpub.controller.PostController
 * @see ru.bechol.devpub.service.VoteService
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostIdRequest {

	long post_id;

	public long getPostId() {
		return this.post_id;
	}
}
