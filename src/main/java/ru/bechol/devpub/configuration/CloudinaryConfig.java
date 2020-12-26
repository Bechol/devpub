package ru.bechol.devpub.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

/**
 * Класс CloudinaryConfig.
 * Конфигурация для подключения к сервису Cloudinary
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.IStorageService
 * @see ru.bechol.devpub.controller.ImageController
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class CloudinaryConfig {

	@Value("${cloudinary.cloud-name}")
	String cloudName;
	@Value("${cloudinary.apikey}")
	String apiKey;
	@Value("${cloudinary.api-secret}")
	String apiSecret;

	@Bean
	public Cloudinary createCloudinaryClient() {
		return new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
	}
}
