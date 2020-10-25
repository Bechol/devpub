package ru.bechol.devpub.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bechol.devpub.response.GeneralInfoResponse;

/**
 * Класс GeneralInfoConfig.
 * Бин для чтения основных параметров блога.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Configuration
public class GeneralInfoConfig {

    @Value("${default.title}")
    private String title;
    @Value("${default.subtitle}")
    private String subtitle;
    @Value("${default.phone}")
    private String phone;
    @Value("${default.email}")
    private String email;
    @Value("${default.copyright}")
    private String copyright;
    @Value("${default.copyrightFrom}")
    private String copyrightFrom;

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
