package ru.bechol.devpub.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.request.EmailRequest;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.AuthorizationResponse;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.response.RegistrationResponse;
import ru.bechol.devpub.service.CaptchaCodesService;
import ru.bechol.devpub.service.UserService;

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
    @Autowired
    private Map<String, Long> sessionMap;

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
     * @return ResponseEntity<RegistrationResponse>.
     */
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                         BindingResult bindingResult) {
        if (!captchaCodesService.captchaIsExist(registerRequest.getCaptcha(), registerRequest.getCaptcha_secret())) {
            bindingResult.addError(new FieldError(
                    "captcha", "captcha", "Код с картинки введён неверно"));
        }
        if (bindingResult.hasErrors()) {
            return userService.registrateWithValidationErrors(bindingResult);
        }
        return userService.registrateNewUser(registerRequest);
    }

    /**
     * Метод check.
     * GET запрос /api/auth/check
     * Проверка наличия идентификатора текущей сессии в списке авторизованных.
     *
     * @param request запрос от клиента.
     * @return информация о текущем авторизованном пользователе, если он авторизован.
     * @see AuthorizationResponse
     */
    @GetMapping("/check")
    public ResponseEntity<AuthorizationResponse> check(HttpServletRequest request) {
        return userService.checkAuthorization(request);
    }

    /**
     * Метод restorePassword.
     * POST запрос /api/auth/restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если пользователь найден, ему отправляется письмо со
     * ссылкой на восстановление пароля.
     *
     * @param emailRequest - данные с пользовательской формы ввода.
     * @return ResponseEntity<AuthorizationResponse>.
     */
    @PostMapping("/restore")
    public ResponseEntity<AuthorizationResponse> restorePassword(@RequestBody EmailRequest emailRequest) {
        if (!Strings.isNotEmpty(emailRequest.getEmail())) {
            return ResponseEntity.ok().body(AuthorizationResponse.builder().result(false).build());
        }
        return userService.checkAndSendForgotPasswordMail(emailRequest.getEmail());
    }

}
