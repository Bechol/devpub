package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.ChangePasswordRequest;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.service.CaptchaCodesService;
import ru.bechol.devpub.service.UserService;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

import javax.management.relation.RoleNotFoundException;
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
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных (таблица captcha_codes) и
     * возвращает секретный код secret (поле в базе данных secret_code) и изображение размером 100х35 с отображённым
     * на ней основным кодом капчи image (поле базе данных code).
     * Также удаляет устаревшие капчи из таблицы. Время устаревания задано в конфигурации приложения (по умолчанию, 1 час).
     *
     * @return ResponseEntity<CaptchaResponse>.
     * @throws IOException
     * @see CaptchaResponse
     */
    @GetMapping("/captcha")
    public ResponseEntity<?> getCaptcha() throws IOException {
        return captchaCodesService.generateCaptcha();
    }

    /**
     * Метод register.
     * POST запрос /api/auth/register
     * Метод создаёт пользователя в базе данных, если введённые данные верны.
     * Если данные неверные - пользователь не создаётся, а метод возвращает соответствующую ошибку.
     *
     * @param registerRequest данные пользовательской формы регистрации.
     * @param bindingResult   результаты валидации данных пользовательской формы.
     * @return ResponseEntity<?>.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
                                      BindingResult bindingResult) throws RoleNotFoundException, CodeNotFoundException {
        return userService.registrateNewUser(registerRequest, bindingResult);
    }

    /**
     * Метод check.
     * GET запрос /api/auth/check
     * Проверка наличия идентификатора текущей сессии в списке авторизованных.
     *
     * @return информация о текущем авторизованном пользователе, если он авторизован.
     */
    @GetMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal User user) {
        return userService.checkAuthorization(user);
    }

    /**
     * Метод restorePassword.
     * POST запрос /api/auth/restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail.
     * Если пользователь найден, ему отправляется письмо со ссылкой на восстановление пароля.
     *
     * @param emailRequest - email, на который необходимо выслать ссылку для восстановления.
     */
    @PostMapping("/restore")
    public Map<String, Boolean> restorePassword(@Valid @RequestBody EditProfileRequest emailRequest,
                                                BindingResult bindingResult) {
        return userService.checkAndSendForgotPasswordMail(emailRequest.getEmail(), bindingResult);
    }

    /**
     * Метод changePassword.
     * POST запрос /api/auth/password
     * Метод проверяет корректность кода восстановления пароля (параметр code) и корректность кодов капчи:
     * введённый код (параметр captcha) должен совпадать со значением в поле code таблицы captcha_codes,
     * соответствующем пришедшему значению секретного кода
     * (параметр captcha_secret и поле secret_code в таблице базы данных).
     *
     * @param changePasswordRequest - данные с пользовательской формы ввода.
     */
    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest, BindingResult bindingResult) {
        return userService.changePassword(changePasswordRequest, bindingResult);
    }

}
