package ru.bechol.devpub.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.models.CaptchaCodes;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.CaptchaCodesRepository;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.service.EmailService;
import ru.bechol.devpub.service.Messages;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

/**
 * Класс AppEventHandler.
 * Обработчик событий. Принимает событие от AppEventListener и обрабатывает его в зависимости от типа.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see AppEventPublisher
 * @see AppEventListener
 * @see DevpubAppEvent
 */
@Slf4j
@Component
public class AppEventHandler {

    @Autowired
    private Messages messages;
    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailService emailService;


    /**
     * Метод handle.
     * Выбирает метод обработки в заивисимости от типа перехваченного события.
     *
     * @param devpubAppEvent - событие.
     */
    void handle(DevpubAppEvent devpubAppEvent) {
        switch (devpubAppEvent.getEventType()) {
            case SAVE_USER:
                log.info(this.createLogMessage(DevpubAppEvent.EventType.SAVE_USER));
                this.handleSaveUser(devpubAppEvent);
                break;
            case SAVE_POST:
                log.info(this.createLogMessage(DevpubAppEvent.EventType.SAVE_POST));
                this.handleSavePost(devpubAppEvent);
                break;
            case SEND_MAIL:
                log.info(this.createLogMessage(DevpubAppEvent.EventType.SEND_MAIL));
                this.handleSendMailEvent(devpubAppEvent);
                break;
            case DELETE_CAPTCHA:
                log.info(this.createLogMessage(DevpubAppEvent.EventType.DELETE_CAPTCHA));
                this.handleDeleteCaptchaEvent(devpubAppEvent);
                break;
            case SAVE_CAPTCHA:
                log.info(this.createLogMessage(DevpubAppEvent.EventType.SAVE_CAPTCHA));
                this.handleSaveCaptchaEvent(devpubAppEvent);
                break;
            default:
                log.warn(this.createLogMessage(DevpubAppEvent.EventType.UNKNOWN));
        }
    }

    /**
     * Метод handleSaveUser.
     * Сохранение пользователя в базу данных.
     *
     * @param devpubAppEvent - событие сохранения пользователя.
     */
    private void handleSaveUser(DevpubAppEvent<User> devpubAppEvent) {
        log.info("handle {}", devpubAppEvent.toString());
        userRepository.save(devpubAppEvent.getEventObject());
    }

    /**
     * Метод handleSavePost.
     * Сохранение поста в базу данных.
     * Отправка письма модератору.
     * @param devpubAppEvent - событие сохранения сохранения поста.
     */
    private void handleSavePost(DevpubAppEvent<Post> devpubAppEvent) {
        log.info("handle {}", devpubAppEvent.toString());
        String moderatorName = devpubAppEvent.getEventObject().getModerator().getName();
        postRepository.save(devpubAppEvent.getEventObject());
        if(devpubAppEvent.getEventObject().isActive()) {
            emailService.send(devpubAppEvent.getEventObject().getModerator().getEmail(),
                    messages.getMessage("post.moderation-mail-subject"),
                    messages.getMessage("post.moderation-mail", moderatorName)
            );
        }
    }

    /**
     * Метод handleSendMailEvent.
     * Отправка писем.
     *
     * @param devpubAppEvent - событие отправки письма.
     */
    private void handleSendMailEvent(DevpubAppEvent<MimeMessage> devpubAppEvent) {
        log.info("handle {}", devpubAppEvent.toString());
        mailSender.send(devpubAppEvent.getEventObject());
    }

    /**
     * Метод handleDeleteCaptchaEvent.
     * Удаление капчи созданной ранее заданного момента времени.
     *
     * @param devpubAppEvent - событие удаления капчи.
     */
    private void handleDeleteCaptchaEvent(DevpubAppEvent<LocalDateTime> devpubAppEvent) {
        log.info("handle {}", devpubAppEvent.toString());
        captchaCodesRepository.deleteByTimeBefore(devpubAppEvent.getEventObject());
    }

    /**
     * Метод handleSaveCaptchaEvent.
     * Сохранение капчи.
     *
     * @param devpubAppEvent - событие сохранения капчи.
     */
    private void handleSaveCaptchaEvent(DevpubAppEvent<CaptchaCodes> devpubAppEvent) {
        log.info("handle {}", devpubAppEvent.toString());
        captchaCodesRepository.save(devpubAppEvent.getEventObject());
    }

    /**
     * Метод createLogMessage.
     * Создает сообщение для лога с учетом параметров.
     *
     * @param eventType - тип события
     * @return строка для лога
     */
    private String createLogMessage(DevpubAppEvent.EventType eventType) {
        return messages.getMessage("receive.event.description", eventType);
    }

}
