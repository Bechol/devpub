package ru.bechol.devpub.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс ModerationRequest.
 * Тело POST запроса /api/moderation
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @version 1.0
 * @see ru.bechol.devpub.controller.ModerationController
 * @see ru.bechol.devpub.service.PostService
 */
@Setter
@AllArgsConstructor
public class ModerationRequest {

    private Long post_id;
    @Getter
    private String decision;

    public Long getPostId() {
        return post_id;
    }
}
