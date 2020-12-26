package ru.bechol.devpub.service;

import org.springframework.stereotype.Service;

/**
 * Интерфейс IEmailService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IEmailService {
	/**
	 * Метод send.
	 * Отправка писем.
	 *
	 * @param emailTo email получателя.
	 * @param subject тема письма.
	 * @param message текст письма.
	 */
	void send(String emailTo, String subject, String message);
}
