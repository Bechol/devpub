package ru.bechol.devpub.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import ru.bechol.devpub.response.GeneralInfoResponse;

/**
 * Класс GeneralInfoConfig.
 * Бин для чтения основных параметров блога.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class GeneralInfoConfig {

	@Value("${default.title}")
	String title;
	@Value("${default.subtitle}")
	String subtitle;
	@Value("${default.phone}")
	String phone;
	@Value("${default.email}")
	String email;
	@Value("${default.copyright}")
	String copyright;
	@Value("${default.copyrightFrom}")
	String copyrightFrom;

	@Bean
	public GeneralInfoResponse generalInfoResponse() {
		return GeneralInfoResponse.builder()
				.title(title)
				.subtitle(subtitle)
				.phone(phone)
				.email(email)
				.copyright(copyright)
				.copyrightFrom(copyrightFrom).build();
	}
}
