package ru.bechol.devpub.configuration.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.*;
import java.io.IOException;

/**
 * Класс AppLogoutHandler.
 * Обработчик успешного выхода пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
public class AppLogoutHandler implements LogoutSuccessHandler {

	/**
	 * Метод onLogoutSuccess.
	 * Вызывается при успешном "выходе" пользователя из системы.
	 * Удаляет текущую сессию из хранилища сессии.
	 *
	 * @param request        запроc /api/auth/logout
	 * @param response       всегда возвращает 200Ок "result": true.
	 * @param authentication текущая авторизация.
	 * @throws IOException
	 */
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
								Authentication authentication) throws IOException {
		response.setStatus(200);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write("{\"result\": true}");
	}
}
