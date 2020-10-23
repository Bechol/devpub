package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

/**
 * Класс EmailService.
 * Реализация отправителя писем.
 *
 * @author Oleg Bech
 * @email Oleg071984@gmail.com
 * @see ru.bechol.devpub.configuration.mail.EmailSenderConfig
 * @see ru.bechol.devpub.configuration.mail.EmailServiceAsyncConfig
 */
@Slf4j
@Service
public class EmailService {

    @Value("${mail-sender.sender-name}")
    private String username;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Метод send.
     * Отправка писем.
     *
     * @param emailTo - email получателя.
     * @param subject - тема письма.
     * @param message - текст письма.
     */
    @Async("emailServiceTaskExecutor")
    public void send(String emailTo, String subject, String message) {
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage mailMessage = mailSender.createMimeMessage();
                boolean multipart = false;
                MimeMessageHelper helper = null;
                helper = new MimeMessageHelper(mailMessage, multipart);
                helper.setFrom(username);
                helper.setTo(emailTo);
                helper.setSubject(subject);
                mailMessage.setContent(message, "text/html");
                mailSender.send(mailMessage);
            } catch (MessagingException messagingException) {
                log.warn(messagingException.getMessage());
                messagingException.printStackTrace();
            }
        });
    }
}
