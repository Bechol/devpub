package ru.bechol.devpub.configuration.mail;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.*;

/**
 * Класс EmailSenderConfig.
 * Конфигурация отправщика писем.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.EmailService
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailSenderConfig {

	@Value("${spring.mail.host}")
	String host;
	@Value("${spring.mail.port}")
	int port;
	@Value("${spring.mail.username}")
	String username;
	@Value("${spring.mail.password}")
	String password;
	@Value("${spring.mail.protocol}")
	String protocol;
	@Value("${mail-sender.debug}")
	String debug;

	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setProtocol(protocol);
		mailSender.setDefaultEncoding("UTF-8");
		return mailSender;
	}
}
