package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Comment;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.CommentRepository;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.request.CommentRequest;
import ru.bechol.devpub.response.CommentResponse;
import ru.bechol.devpub.response.ErrorResponse;

import java.security.Principal;

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
    private Messages messages;

    /**
     * Метод addComment.
     * Добавление комментария к посту.
     *
     * @param commentRequest - тело запроса POST /api/comment
     * @param principal      - авторизованный пользователь.
     * @return ResponseEntity<?>.
     */
    public ResponseEntity<?> addComment(CommentRequest commentRequest, Principal principal) {
        String commentPostId = commentRequest.getPostId();
        String postErrorMessage = messages.getMessage("er.post.not-found", commentPostId);
        String userErrorMessage = messages.getMessage("er.user.not-found", "email", principal.getName());
        Post post = postRepository.findById(Long.valueOf(commentPostId)).orElse(null);
        if (post == null) {
            log.warn(postErrorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().message(postErrorMessage));
        }
        User activeUser = userService.findActiveUser(principal);
        if (activeUser == null) {
            log.warn(userErrorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().message(userErrorMessage));
        }
        String parentId = commentRequest.getParentId();
        Comment newPostComment = new Comment();
        newPostComment.setPost(post);
        newPostComment.setUser(activeUser);
        if (Strings.isNotEmpty(parentId)) {
            newPostComment.setParent(commentRepository.findById(Long.valueOf(parentId)).orElse(null));
        }
        newPostComment.setText(commentRequest.getText());
        return ResponseEntity.ok(CommentResponse.builder().id(commentRepository.save(newPostComment).getId()).build());
    }
}
