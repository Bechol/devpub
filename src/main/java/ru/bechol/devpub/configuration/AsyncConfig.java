package ru.bechol.devpub.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
@EnableAsync
public class AsyncConfig {

	@Value("${async-executor.thread-pool.core-pool-size}")
	int corePoolSize;
	@Value("${async-executor.thread-pool.max-pool-size}")
	int maxPoolSize;
	@Value("${async-executor.thread-pool.queue-capacity}")
	int queueCapacity;
	@Value("${async-executor.thread-pool.thread-name-prefix}")
	String threadNamePrefix;

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(threadNamePrefix);
		executor.initialize();
		return executor;
	}
}
