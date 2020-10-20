package ru.bechol.devpub.configuration.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Класс AppLogoutHandler.
 * Обработчик успешного выхода пользователя.
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @version 1.0
 */
public class AppLogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        //todo удаление текущей сессии из таблицы spring_session
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"result\": true}");
    }
}
