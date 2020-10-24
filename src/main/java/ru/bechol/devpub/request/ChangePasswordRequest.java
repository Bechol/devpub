package ru.bechol.devpub.request;

import lombok.Data;
import ru.bechol.devpub.request.validators.annotations.ValidPassword;

/**
 * Класс ChangePasswordRequest.
 * Тело запроса на изменение пароля.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Data
public class ChangePasswordRequest {

    private String code;
    @ValidPassword
    private String password;
    private String captcha;
    private String captcha_secret;
}
