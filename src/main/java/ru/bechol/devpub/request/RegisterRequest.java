package ru.bechol.devpub.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bechol.devpub.request.validators.annotations.*;

import javax.validation.constraints.NotEmpty;

/**
 * Класс RegisterRequest.
 * Для десеарилизации тела запросов на регистрацию пользователя.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.ApiAuthController
 * @see ru.bechol.devpub.service.IUserService
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

	@JsonProperty("e_mail")
	@ExistEmailValidation
	String email;
	@ValidPassword
	String password;
	@NotEmpty(message = "Имя указано неверно")
	String name;
	String captcha;
	String captcha_secret;

}
