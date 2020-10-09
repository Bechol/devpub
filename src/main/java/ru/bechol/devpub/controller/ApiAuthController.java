package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.service.CaptchaCodesService;

import java.io.IOException;

/**
 * Класс ApiAuthController.
 * REST контроллер для обработки всех запросов через /api/auth/
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CaptchaCodesService
 */
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private CaptchaCodesService captchaCodesService;

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        return captchaCodesService.generateCaptcha();
    }
}
