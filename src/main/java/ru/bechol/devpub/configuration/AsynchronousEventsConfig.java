package ru.bechol.devpub.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import ru.bechol.devpub.event.AppEventListener;

@Configuration
public class AsynchronousEventsConfig {

    @Autowired
    private AppEventListener appEventListener;

    @Bean
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.addApplicationListener(appEventListener);
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor("EventExecutor"));
        return eventMulticaster;
    }

}
