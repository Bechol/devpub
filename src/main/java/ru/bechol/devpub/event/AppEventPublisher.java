package ru.bechol.devpub.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.service.Messages;

/**
 * Класс AppEventPublisher.
 * Публикация событий.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Slf4j
@Component
public class AppEventPublisher implements ApplicationEventPublisher {

    @Autowired
    private Messages messages;
    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }

    @Override
    public void publishEvent(Object o) {
        if (o instanceof ApplicationEvent) {
            this.publishEvent(o);
        }
    }
}
