package ru.bechol.devpub.service.exception;

/**
 * Класс ModeratorNotFoundException.
 * Исключение в ModeratorLoadBalancer когда модератор не найден.
 *
 * @author Oleg Bech;
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.helper.ModeratorLoadBalancer
 */
public class ModeratorNotFoundException extends Exception {

    public ModeratorNotFoundException(String msg) {
        super(msg);
    }
}
