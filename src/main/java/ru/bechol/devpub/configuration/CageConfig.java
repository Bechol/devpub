package ru.bechol.devpub.configuration;

import com.github.cage.Cage;
import com.github.cage.image.Painter;
import com.github.cage.token.RandomTokenGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.util.Random;

/**
 * Класс CageConfig.
 * Конфигурация генератора капчи.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class CageConfig {

	@Value("${captcha.image.width}")
	int width;
	@Value("${captcha.image.height}")
	int height;
	@Value("${captcha.image.format}")
	String format;
	@Value("${captcha.image.compress-ratio}")
	float ratio;
	@Value("${captcha.token.min-length}")
	int minLength;
	@Value("${captcha.token.delta}")
	int delta;

	@Bean
	public Cage cage() {
		Painter painter = new Painter(width, height, null, null, null, null);
		RandomTokenGenerator tokenGenerator = new RandomTokenGenerator(new Random(), minLength, delta);
		return new Cage(painter, null, null, format, ratio, tokenGenerator, null);
	}
}
