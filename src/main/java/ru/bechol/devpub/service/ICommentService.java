package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.CommentRequest;
import ru.bechol.devpub.service.exception.PostNotFoundException;

/**
 * Интерфейс ICommentService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface ICommentService {

	/**
	 * Метод addComment.
	 * Добавление комментария к посту.
	 *
	 * @param commentRequest тело запроса.
	 * @param user           авторизованный пользователь.
	 * @return ResponseEntity<?>.
	 */
	ResponseEntity<?> addComment(CommentRequest commentRequest, User user, BindingResult bindingResult)
			throws PostNotFoundException;


}
