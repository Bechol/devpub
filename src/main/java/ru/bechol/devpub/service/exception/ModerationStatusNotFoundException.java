package ru.bechol.devpub.service.exception;

/**
 * Класс ModerationStatusNotFoundException.
 * Исключение, возникающее при выборе несуществующего статуса модерации.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class ModerationStatusNotFoundException extends Exception {

    public ModerationStatusNotFoundException(String message) {
        super(message);
    }
}
