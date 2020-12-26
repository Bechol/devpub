package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.*;

import java.util.Map;

/**
 * Интерфейс IUserService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
public interface IUserService extends UserDetailsService {

	/**
	 * Метод registrateNewUser.
	 * Регистрация пользователя.
	 *
	 * @param registerRequest тело запроса.
	 * @return ResponseEntity<?>.
	 */
	ResponseEntity<?> registrateNewUser(RegisterRequest registerRequest, BindingResult bindingResult)
			throws Exception;

	/**
	 * Метод isUserNotExistByEmail.
	 * Проверяет, существует что пользователь с данным email-ом.
	 *
	 * @param email почта пользователя.
	 * @return true если пользователь не существует.
	 */
	boolean isUserNotExistByEmail(String email);


	/**
	 * Метод checkAuthorization.
	 * Метод возвращает информацию о текущем авторизованном пользователе.
	 *
	 * @param authorizedUser авторизованный пользователь.
	 * @return ResponseEntity.
	 */
	ResponseEntity<?> checkAuthorization(User authorizedUser);

	/**
	 * Метод checkAndSendForgotPasswordMail.
	 * Формирование и отправка письма с ссылкой для восстановления пароля.
	 *
	 * @param email         email для отправки.
	 * @param bindingResult результат валидации данных, ввуденых пользователем
	 */
	Map<String, Boolean> checkAndSendForgotPasswordMail(String email, BindingResult bindingResult);

	/**
	 * Метод changePassword.
	 * Изменение пароля пользователя.
	 *
	 * @param changePasswordRequest тело запроса на изменение пароля.
	 * @param bindingResult         результаты валидации данных.
	 */
	ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest, BindingResult bindingResult);
}
