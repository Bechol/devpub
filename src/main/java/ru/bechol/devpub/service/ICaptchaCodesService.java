package ru.bechol.devpub.service;


import org.springframework.stereotype.Service;
import ru.bechol.devpub.response.CaptchaResponse;

import java.io.IOException;

/**
 * Интерфейс ICaptchaCodesService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface ICaptchaCodesService {

	/**
	 * Метод generateCaptcha.
	 * Генерация новой капчи.
	 *
	 * @return ResponseEntity<CaptchaResponse>
	 * @throws IOException
	 */
	CaptchaResponse generateCaptcha() throws IOException;

	/**
	 * Метод captchaIsExist.
	 * Проверка существования капчи.
	 *
	 * @param code       код с картинки.
	 * @param secretCode секретный код.
	 * @return true если капча найдена.
	 */
	boolean captchaIsExist(String code, String secretCode);

}
