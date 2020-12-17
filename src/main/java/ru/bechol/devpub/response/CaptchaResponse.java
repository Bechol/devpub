package ru.bechol.devpub.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс CaptchaResponse.
 * Сериализация капчи.
 *
 * @author Oleg Bech
 * @author oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.ApiAuthController
 * @see ru.bechol.devpub.service.CaptchaCodesService
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaptchaResponse {

    String code;
    String secret;
    String image;
}
