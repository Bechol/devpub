package ru.bechol.devpub.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс CaptchaCodes.
 * Доменный объект, представляющий код капчи.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.CaptchaCodesRepository
 * @see ru.bechol.devpub.service.CaptchaCodesService
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes extends BaseEntity {

	@UpdateTimestamp
	@Column(columnDefinition = "timestamp with time zone", nullable = false)
	LocalDateTime time;
	@Column(nullable = false)
	String code;
	@Column(name = "secret_code", nullable = false)
	String secretCode;

}
