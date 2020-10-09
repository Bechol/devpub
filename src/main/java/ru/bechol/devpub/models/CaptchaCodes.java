package ru.bechol.devpub.models;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс CaptchaCodes.
 * Реализация сущности кода капчи.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.CaptchaCodesRepository
 * @see ru.bechol.devpub.service.CaptchaCodesService
 */
@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @UpdateTimestamp
    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private LocalDateTime time;
    @Column(nullable = false)
    private String code;
    @Column(name = "secret_code", nullable = false)
    private String secretCode;

}
