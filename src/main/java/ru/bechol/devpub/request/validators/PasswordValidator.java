package ru.bechol.devpub.request.validators;


import org.apache.logging.log4j.util.Strings;
import ru.bechol.devpub.request.validators.annotations.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Класс PasswordValidator.
 * Проверка введенного пользователем пароля на длину.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return Strings.isNotEmpty(password) && password.length() > 6;
    }

}
