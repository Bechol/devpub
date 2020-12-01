package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.event.DevpubAppEvent;
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
@Slf4j
@Service
@Trace
public class EmailService {

    @Value("${mail-sender.sender-name}")
    private String username;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Метод send.
     * Отправка писем.
     *
     * @param emailTo - email получателя.
     * @param subject - тема письма.
     * @param message - текст письма.
     */
    public void send(String emailTo, String subject, String message) {
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            boolean multipart = false;
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(mailMessage, multipart);
            helper.setFrom(username);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            mailMessage.setContent(message, "text/html");
            applicationEventPublisher.publishEvent(new DevpubAppEvent<>(
                    this, mailMessage, DevpubAppEvent.EventType.SEND_MAIL
            ));
        } catch (MessagingException messagingException) {
            log.warn(messagingException.getMessage());
            messagingException.printStackTrace();
        }
    }
}
