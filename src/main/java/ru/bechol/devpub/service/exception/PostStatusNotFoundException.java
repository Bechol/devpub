package ru.bechol.devpub.service.exception;

/**
 * Класс PostStatusNotFoundException.
 * Исключение, возникающее при выборе несуществующего статуса поста.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class PostStatusNotFoundException extends Exception {

    public PostStatusNotFoundException(String message) {
        super(message);
    }
}
