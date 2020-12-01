package ru.bechol.devpub.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Класс AppEventListener.
 * Слушатель событий. Перехватывает событие отправляет в AppEventHandler.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see AppEventHandler
 */
@Slf4j
@Component
public class AppEventListener implements ApplicationListener<DevpubAppEvent<?>> {

    @Autowired
    private AppEventHandler appEventHandler;

    @Override
    public void onApplicationEvent(DevpubAppEvent devpubAppEvent) {
        appEventHandler.handle(devpubAppEvent);
    }

}
