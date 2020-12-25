package ru.bechol.devpub.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.service.aspect.Trace;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Класс EmailService.
 * Реализация отправителя писем.
 *
 * @author Oleg Bech
 * @email Oleg071984@gmail.com
 * @see ru.bechol.devpub.configuration.mail.EmailSenderConfig
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Async("asyncExecutor")
@Service
public class EmailService {

	@Value("${mail-sender.sender-name}")
	String senderName;
	@Autowired
	JavaMailSender mailSender;
	@Autowired
	Messages messages;

	/**
	 * Метод send.
	 * Отправка писем.
	 *
	 * @param emailTo - email получателя.
	 * @param subject - тема письма.
	 * @param message - текст письма.
	 */
	@Trace
	public void send(String emailTo, String subject, String message) {
		try {
			MimeMessage mailMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
			helper.setFrom(senderName);
			helper.setTo(emailTo);
			helper.setSubject(subject);
			mailMessage.setContent(message, "text/html; charset=UTF-8");
			mailSender.send(mailMessage);
		} catch (MessagingException messagingException) {
			messagingException.printStackTrace();
		}
	}
}
