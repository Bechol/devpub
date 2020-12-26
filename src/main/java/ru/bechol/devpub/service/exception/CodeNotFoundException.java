package ru.bechol.devpub.service.exception;

/**
 * Класс CodeNotFoundException.
 * Исключение, возникающее при поиске несуществующей настройки.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class CodeNotFoundException extends Exception {

	public CodeNotFoundException(String message) {
		super(message);
	}
}
