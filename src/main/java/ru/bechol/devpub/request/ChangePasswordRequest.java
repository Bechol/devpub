package ru.bechol.devpub.request;

import lombok.Data;
import ru.bechol.devpub.request.validators.annotations.ValidPassword;

import javax.validation.constraints.NotEmpty;

/**
 * Класс ChangePasswordRequest.
 * Тело запроса на изменение пароля.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Data
public class ChangePasswordRequest {

    @NotEmpty
    private String code;
    @ValidPassword
    private String password;
    @NotEmpty
    private String captcha;
    @NotEmpty
    private String captcha_secret;
}
