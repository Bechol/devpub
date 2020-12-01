package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.request.CommentRequest;
import ru.bechol.devpub.service.CommentService;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Класс CommentController.
 * REST контроллер для обработки всех запросов через /api/comment
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CommentService
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Метод commentPost.
     * POST запрос /api/comment
     * Добавление комментария к посту.
     *
     * @param commentRequest - тело запроса.
     * @param bindingResult  - результаты валидации тела запроса.
     * @param principal      - авторизованный пользователь.
     * @return ResponseEntity<?>.
     */
    @PostMapping
    public ResponseEntity<?> commentPost(@Valid @RequestBody CommentRequest commentRequest, BindingResult bindingResult,
                                         Principal principal) throws PostNotFoundException {
        return commentService.addComment(commentRequest, principal, bindingResult);
    }
}
