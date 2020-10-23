package ru.bechol.devpub.configuration.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Класс EmailServiceAsyncConfig.
 * Конфигурация отдельного executor-а для отправки почты.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.EmailService
 */
@Configuration
@EnableAsync
public class EmailServiceAsyncConfig {

    @Value("${mail-sender.executor.core-pool-size}")
    private int corePoolSize;
    @Value("${mail-sender.executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${mail-sender.executor.queue-capacity}")
    private int queueCapacity;
    @Value("${mail-sender.executor.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "emailServiceTaskExecutor")
    public Executor emailServiceTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
