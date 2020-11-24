package ru.bechol.devpub.service.exception;

import lombok.Getter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Класс UserNotFoundException.
 * Исключение, возникающее при поиске пользователя по определенному полю, если таковой не найден.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Getter
public class UserNotFoundException extends UsernameNotFoundException {

    private String field;
    private String fieldValue;

    public UserNotFoundException(String msg, String searchByField, String searchValue) {
        super(msg);
        this.field = searchByField;
        this.fieldValue = searchValue;
    }
}
