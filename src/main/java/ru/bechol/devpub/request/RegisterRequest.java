package ru.bechol.devpub.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bechol.devpub.request.validators.annotations.ExistEmailValidation;
import ru.bechol.devpub.request.validators.annotations.ValidPassword;

import javax.validation.constraints.NotEmpty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {

    @JsonProperty("e_mail")
    @ExistEmailValidation
    private String email;
    @ValidPassword
    private String password;
    @NotEmpty(message = "Имя указано неверно")
    private String name;
    private String captcha;
    private String captcha_secret;

}
