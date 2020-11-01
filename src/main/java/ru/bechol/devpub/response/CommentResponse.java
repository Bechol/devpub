package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Класс CommentResponse.
 * Ответ на POST запрос /api/comment
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.CommentService
 * @see ru.bechol.devpub.controller.CommentController
 */
@Getter
@Builder
public class CommentResponse {

    private long id;
}
