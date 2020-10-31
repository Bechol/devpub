package ru.bechol.devpub.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс PostIdRequest.
 * Тело запроса GET /api/post/like
 *
 * @author Oleg Bech
 * @version 1.0
 * @see ru.bechol.devpub.controller.PostController
 */
@Data
@NoArgsConstructor
public class PostIdRequest {

    private long post_id;

    public long getPostId() {
        return this.post_id;
    }
}
