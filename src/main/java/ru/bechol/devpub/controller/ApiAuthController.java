package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.ChangePasswordRequest;
import ru.bechol.devpub.request.EmailRequest;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.CaptchaCodesService;
import ru.bechol.devpub.service.UserService;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * Класс ApiAuthController.
 * REST контроллер для обработки всех запросов через /api/auth/
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CaptchaCodesService
 * @see UserService
 */
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private CaptchaCodesService captchaCodesService;
    @Autowired
    private UserService userService;

    /**
     * Метод getCaptcha.
     * GET запрос /api/auth/captcha
     * Генерация капчи. Сохранение в таблицу captcha_codes. Удаление устаревших записей из таблицы captcha_codes.
     *
     * @return ResponseEntity<CaptchaResponse>.
     * @throws IOException
     * @see CaptchaResponse
     */
    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        return captchaCodesService.generateCaptcha();
    }

    /**
     * Метод register.
     * POST запрос /api/auth/register
     * Регистрация пользователя.
     *
     * @param registerRequest данные пользовательской формы регистрации.
     * @param bindingResult   результаты валидации данных пользовательской формы.
     * @return ResponseEntity<?>.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
                                      BindingResult bindingResult) throws RoleNotFoundException {
        return userService.registrateNewUser(registerRequest, bindingResult);
    }

    /**
     * Метод check.
     * GET запрос /api/auth/check
     * Проверка наличия идентификатора текущей сессии в списке авторизованных.
     *
     * @param request запрос от клиента.
     * @return информация о текущем авторизованном пользователе, если он авторизован.
     */
    @GetMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return userService.checkAuthorization(user);
    }

    /**
     * Метод restorePassword.
     * POST запрос /api/auth/restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если пользователь найден, ему отправляется письмо со
     * ссылкой на восстановление пароля.
     *
     * @param emailRequest - данные с пользовательской формы ввода.
     */
    @PostMapping("/restore")
    public ResponseEntity<?> restorePassword(@Valid @RequestBody EmailRequest emailRequest,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.ok().body(Response.builder().result(false).build());
        }
        return userService.checkAndSendForgotPasswordMail(emailRequest.getEmail());
    }

    /**
     * Метод changePassword.
     * POST запрос /api/auth/password
     * Проверка данных запроса. Изменение пароля пользователя .
     *
     * @param changePasswordRequest - данные с пользовательской формы ввода.
     */
    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest, BindingResult bindingResult) {
        return userService.changePassword(changePasswordRequest, bindingResult);
    }

}
