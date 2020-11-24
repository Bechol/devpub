package ru.bechol.devpub.service.exception;

/**
 * Класс SortModeNotFoundException.
 * Исключение, возникающее при выборе несуществующего режима сортировки.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class SortModeNotFoundException extends Exception {

    public SortModeNotFoundException(String message) {
        super(message);
    }
}
