package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.repository.*;
import ru.bechol.devpub.request.CommentRequest;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.exception.PostNotFoundException;

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
 * @see ICommentRepository
 * @see ru.bechol.devpub.controller.CommentController
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class CommentService implements ICommentService {

	@Autowired
	ICommentRepository commentRepository;
	@Autowired
	IPostRepository postRepository;
	@Autowired
	@Qualifier("userService")
	private IUserService userService;
	@Autowired
	@Qualifier("postService")
	IPostService postService;
	@Autowired
	Messages messages;

	/**
	 * Метод addComment.
	 * Добавление комментария к посту.
	 *
	 * @param commentRequest тело запроса POST /api/comment
	 * @param user           авторизованный пользователь.
	 * @return ResponseEntity<?>.
	 */
	@Override
	public ResponseEntity<?> addComment(CommentRequest commentRequest, User user, BindingResult bindingResult)
			throws PostNotFoundException {
		if (bindingResult.hasErrors()) {
			return createBindingErrorResponse(bindingResult, HttpStatus.BAD_REQUEST);
		}
		String commentPostId = commentRequest.getPostId();
		String parentId = commentRequest.getParentId();
		Comment newPostComment = new Comment();
		newPostComment.setPost(postService.findById(Long.parseLong(commentPostId)));
		newPostComment.setUser(user);
		if (Strings.isNotEmpty(parentId)) {
			newPostComment.setParent(commentRepository.findById(Long.valueOf(parentId)).orElse(null));
		}
		newPostComment.setText(commentRequest.getText());
		return ResponseEntity.ok(Map.of("id", commentRepository.save(newPostComment).getId()));
	}
}
