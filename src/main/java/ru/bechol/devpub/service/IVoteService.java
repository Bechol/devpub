package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.PostIdRequest;
import ru.bechol.devpub.service.exception.PostNotFoundException;

/**
 * Интерфейс IVoteService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IVoteService {

	/**
	 * Метод like.
	 * Лайк/дизлайк поста. Операция зависит от переданного значения value.
	 *
	 * @param postIdRequest id поста.
	 * @param user          авторизованный пользователь.
	 * @param value         1:like, -1:dislike
	 * @return Response.
	 */
	ResponseEntity<?> vote(PostIdRequest postIdRequest, User user, int value) throws PostNotFoundException;

}
