package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Post;
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
public class EmailService {

    @Value("${mail-sender.sender-name}")
    private String username;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Messages messages;

    /**
     * Метод send.
     * Отправка писем.
     *
     * @param emailTo - email получателя.
     * @param subject - тема письма.
     * @param message - текст письма.
     */
    @Trace
    @Async("asyncExecutor")
    public void send(String emailTo, String subject, String message) {
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            boolean multipart = false;
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(mailMessage, multipart);
            helper.setFrom(username);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            mailMessage.setContent(message, "text/html; charset=UTF-8");
            mailSender.send(mailMessage);
        } catch (MessagingException messagingException) {
            log.warn(messagingException.getMessage());
            messagingException.printStackTrace();
        }
    }

    public void send(Post post) {
        log.info("send notification email to moderator [{}]",  post.getModerator().getEmail());
        this.send(post.getModerator().getEmail(),
                messages.getMessage("post.moderation-mail-subject"),
                messages.getMessage("post.moderation-mail", post.getModerator().getName())
        );
    }
}
