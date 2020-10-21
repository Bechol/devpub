package ru.bechol.devpub.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс SessionMapConfig.
 * Создание бина - хранилища для сессий.
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @version 1.0
 */
@Configuration
public class SessionMapConfig {

    @Bean
    public Map<String, Long> sessionMap() {
        Map<String, Long> sessionMap = new HashMap<>();
        return sessionMap;
    }
}
