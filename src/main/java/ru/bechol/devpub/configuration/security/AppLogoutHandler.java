package ru.bechol.devpub.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Класс AppLogoutHandler.
 * Обработчик успешного выхода пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
public class AppLogoutHandler implements LogoutSuccessHandler {

    @Autowired
    private Map<String, Long> sessionMap;

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
        sessionMap.remove(request.getSession().getId());
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"result\": true}");
    }
}
