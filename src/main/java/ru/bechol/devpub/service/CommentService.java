package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.Comment;
import ru.bechol.devpub.repository.*;
import ru.bechol.devpub.request.CommentRequest;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import java.security.Principal;
import java.util.Map;

import static ru.bechol.devpub.service.helper.ErrorMapHelper.createBindingErrorResponse;

/**
 * Класс CommentService.
 * Сервисный слой для Comment.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Comment
 * @see CommentRepository
 * @see ru.bechol.devpub.controller.CommentController
 */
@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private Messages messages;

    /**
     * Метод addComment.
     * Добавление комментария к посту.
     *
     * @param commentRequest - тело запроса POST /api/comment
     * @param principal      - авторизованный пользователь.
     * @return ResponseEntity<?>.
     */
    public ResponseEntity<?> addComment(CommentRequest commentRequest, Principal principal, BindingResult bindingResult)
            throws PostNotFoundException {
        if (bindingResult.hasErrors()) {
            return createBindingErrorResponse(bindingResult, HttpStatus.BAD_REQUEST);
        }
        String commentPostId = commentRequest.getPostId();
        String parentId = commentRequest.getParentId();
        Comment newPostComment = new Comment();
        newPostComment.setPost(postService.findById(Long.parseLong(commentPostId)));
        newPostComment.setUser(userService.findByEmail(principal.getName()));
        if (Strings.isNotEmpty(parentId)) {
            newPostComment.setParent(commentRepository.findById(Long.valueOf(parentId)).orElse(null));
        }
        newPostComment.setText(commentRequest.getText());
        return ResponseEntity.ok(Map.of("id", commentRepository.save(newPostComment).getId()));
    }
}
