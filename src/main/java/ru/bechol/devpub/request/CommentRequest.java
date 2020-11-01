package ru.bechol.devpub.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

/**
 * Класс CommentRequest.
 * Тело POST запроса /api/comment
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.CommentController
 */
@Setter
@AllArgsConstructor
public class CommentRequest {

    private String parent_id;
    private String post_id;
    @Getter
    @Size(min = 20, max = 500, message = "Текст комментария может быть размером от 20-ти до 500-а символов.")
    private String text;

    public String getParentId() {
        return parent_id;
    }

    public String getPostId() {
        return post_id;
    }
}
