package ru.bechol.devpub.service.exception;

/**
 * Класс PostNotFoundException.
 * Исключение, возникающее при поиске поста, если таковой не найден.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class PostNotFoundException extends Exception {

	public PostNotFoundException(String msg) {
		super(msg);
	}
}
