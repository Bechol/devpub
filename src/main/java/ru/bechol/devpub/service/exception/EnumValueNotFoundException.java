package ru.bechol.devpub.service.exception;

/**
 * Класс EnumValueNotFoundException.
 * Исключение, возникающее при ошибке поиска по значению.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
public class EnumValueNotFoundException extends Exception {

	public EnumValueNotFoundException(String message) {
		super(message);
	}
}
