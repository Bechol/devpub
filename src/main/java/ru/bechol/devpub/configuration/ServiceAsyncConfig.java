package ru.bechol.devpub.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Класс ServiceAsyncConfig.
 * Конфигурация executor-ов.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.EmailService
 */
@Configuration
@EnableAsync
public class ServiceAsyncConfig {

    @Value("${async.executors.mail-sender.core-pool-size}")
    private int mailSenderCorePoolSize;
    @Value("${async.executors.mail-sender.max-pool-size}")
    private int mailSenderMaxPoolSize;
    @Value("${async.executors.mail-sender.queue-capacity}")
    private int mailSenderQueueCapacity;
    @Value("${async.executors.mail-sender.thread-name-prefix}")
    private String mailSenderThreadNamePrefix;

    @Value("${async.executors.post-service.core-pool-size}")
    private int postServiceCorePoolSize;
    @Value("${async.executors.post-service.max-pool-size}")
    private int postServiceMaxPoolSize;
    @Value("${async.executors.post-service.queue-capacity}")
    private int postServiceQueueCapacity;
    @Value("${async.executors.post-service.thread-name-prefix}")
    private String postServiceThreadNamePrefix;

    @Bean(name = "emailServiceTaskExecutor")
    public Executor emailServiceTaskExecutor() {
        ThreadPoolTaskExecutor mailSenderExecutor = new ThreadPoolTaskExecutor();
        mailSenderExecutor.setCorePoolSize(mailSenderCorePoolSize);
        mailSenderExecutor.setMaxPoolSize(mailSenderMaxPoolSize);
        mailSenderExecutor.setQueueCapacity(mailSenderQueueCapacity);
        mailSenderExecutor.setThreadNamePrefix(mailSenderThreadNamePrefix);
        mailSenderExecutor.initialize();
        return mailSenderExecutor;
    }

    @Bean(name = "postServiceTaskExecutor")
    public Executor postServiceTaskExecutor() {
        ThreadPoolTaskExecutor postServiceExecutor = new ThreadPoolTaskExecutor();
        postServiceExecutor.setCorePoolSize(postServiceCorePoolSize);
        postServiceExecutor.setMaxPoolSize(postServiceMaxPoolSize);
        postServiceExecutor.setQueueCapacity(postServiceQueueCapacity);
        postServiceExecutor.setThreadNamePrefix(postServiceThreadNamePrefix);
        postServiceExecutor.initialize();
        return postServiceExecutor;
    }
}
