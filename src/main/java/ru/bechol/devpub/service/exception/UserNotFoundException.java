package ru.bechol.devpub.service.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Класс UserNotFoundException.
 * Исключение, возникающее при поиске пользователя по определенному полю, если таковой не найден.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UserNotFoundException extends UsernameNotFoundException {

	String field;
	String fieldValue;

	public UserNotFoundException(String msg, String searchByField, String searchValue) {
		super(msg);
		this.field = searchByField;
		this.fieldValue = searchValue;
	}
}
