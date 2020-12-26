package ru.bechol.devpub.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bechol.devpub.request.validators.annotations.ValidPassword;

import javax.validation.constraints.NotEmpty;

/**
 * Класс ChangePasswordRequest.
 * Для десеарилизации тела запроса на изменение пароля.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.ApiAuthController
 * @see ru.bechol.devpub.service.IUserService
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

	@NotEmpty
	String code;
	@ValidPassword
	String password;
	@NotEmpty
	String captcha;
	@NotEmpty
	String captcha_secret;
}
