package ru.bechol.devpub.request;

import lombok.Data;
import ru.bechol.devpub.request.validators.annotations.ExistEmailValidation;
import ru.bechol.devpub.request.validators.annotations.ValidPassword;

import javax.validation.constraints.NotEmpty;

@Data
public class RegisterRequest {

    @ExistEmailValidation
    private String e_mail;
    @ValidPassword
    private String password;
    @NotEmpty(message = "Имя указано неверно")
    private String name;
    private String captcha;
    private String captcha_secret;

}
