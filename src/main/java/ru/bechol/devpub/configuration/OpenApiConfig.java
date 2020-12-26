package ru.bechol.devpub.configuration;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

/**
 * Класс OpenApiConfig.
 * Конфиг для заголовка документации swagger.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components())
				.info(new Info().title("Skillbox Devpub API").description(
						"Описание API для работы с блогом. Дипломный проект Skillbox"));
	}
}
