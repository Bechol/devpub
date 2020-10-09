package ru.bechol.devpub.configuration;

import com.github.cage.Cage;
import com.github.cage.image.Painter;
import com.github.cage.token.RandomTokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class CageConfig {

    @Value("${captcha.image.width}")
    private int width;
    @Value("${captcha.image.height}")
    private int height;
    @Value("${captcha.image.format}")
    private String format;
    @Value("${captcha.image.compress-ratio}")
    private float ratio;
    @Value("${captcha.token.min-length}")
    private int minLength;
    @Value("${captcha.token.delta}")
    private int delta;

    @Bean
    public Cage cage() {
        Painter painter = new Painter(width, height, null, null, null, null);
        RandomTokenGenerator tokenGenerator = new RandomTokenGenerator(new Random(), minLength, delta);
        return new Cage(painter, null, null, format, ratio, tokenGenerator, null);
    }
}
