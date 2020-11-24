package ru.bechol.devpub.request.validators;


import org.springframework.beans.factory.annotation.Autowired;
import ru.bechol.devpub.request.validators.annotations.ExistEmailValidation;
import ru.bechol.devpub.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Класс EmailValidator.
 * Проверка введенного пользователем почтового адреса на соответствие шаблону.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class EmailValidator implements ConstraintValidator<ExistEmailValidation, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(ExistEmailValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return userService.findByEmail(email) == null;
    }
}
