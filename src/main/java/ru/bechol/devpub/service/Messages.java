package ru.bechol.devpub.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Класс Messages.
 * Выдача сообщений, в зависимости от локали
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class Messages {

	/**
	 * Класс для обращения к файлам локли по имени
	 */
	ResourceBundleMessageSource messageSource;

	/**
	 * Локаль приложения
	 */
	@Value("${spring.mvc.locale}")
	String localeFromProperties;

	@Autowired
	public Messages(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param id id параметра в пропертях
	 * @return сообщение, хранимое в пропертях
	 */
	public String getMessage(String id) {
		Locale locale = new Locale(localeFromProperties);
		return messageSource.getMessage(id, null, locale);
	}

	/**
	 * @param id    id параметра в пропертях
	 * @param param Список параметров, которые участвуют в отображении
	 * @return сообщение, хранимое в пропертях
	 */
	public String getMessage(String id, Object... param) {
		Locale locale = new Locale(localeFromProperties);
		return messageSource.getMessage(id, param, locale);
	}
}
