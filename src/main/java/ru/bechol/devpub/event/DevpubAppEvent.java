package ru.bechol.devpub.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * Класс DevpubAppEvent.
 * Реализация события.
 *
 * @param <T> - объект передаваемый в событии.
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see AppEventListener
 * @see AppEventPublisher
 * @see ru.bechol.devpub.configuration.AsynchronousEventsConfig
 */
@Slf4j
@Getter
public class DevpubAppEvent<T> extends ApplicationEvent {

    private final T eventObject;
    private final EventType eventType;

    public DevpubAppEvent(Object source, T eventObject, EventType eventType) {
        super(source);
        this.eventObject = eventObject;
        this.eventType = eventType;
    }

    public enum EventType {
        SAVE_USER, SEND_MAIL, DELETE_CAPTCHA, SAVE_CAPTCHA, SAVE_POST, UNKNOWN;
    }
}
