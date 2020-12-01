package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Класс CaptchaResponse.
 * Сериализация ответа на GET запрос /api/auth/captcha.
 *
 * @author Oleg Bech
 * @author oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.ApiAuthController
 * @see ru.bechol.devpub.service.CaptchaCodesService
 */
@Getter
@Builder
public class CaptchaResponse {

    private String code;
    private String secret;
    private String image;
}
