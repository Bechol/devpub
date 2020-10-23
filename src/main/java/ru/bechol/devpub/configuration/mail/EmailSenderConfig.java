package ru.bechol.devpub.configuration.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Класс EmailSenderConfig.
 * Конфигурация отправщика писем.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see EmailServiceAsyncConfig
 * @see ru.bechol.devpub.service.EmailService
 */
public class EmailSenderConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @Value("${mail-sender.debug}")
    private String debug;

    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol(protocol);
        mailSender.setDefaultEncoding("UTF-8");

        Properties prop = mailSender.getJavaMailProperties();
        prop.setProperty("mail.debug", debug);

        return mailSender;
    }
}
