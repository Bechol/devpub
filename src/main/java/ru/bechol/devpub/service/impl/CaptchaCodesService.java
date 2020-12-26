package ru.bechol.devpub.service.impl;

import com.github.cage.Cage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.models.CaptchaCodes;
import ru.bechol.devpub.repository.ICaptchaCodesRepository;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.service.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс CaptchaCodesService.
 * Реализация сервисного слоя для CaptchaCodes.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CaptchaCodes
 * @see ICaptchaCodesRepository
 * @see DefaultController
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class CaptchaCodesService implements ICaptchaCodesService {

	@Autowired
	Messages messages;
	@Autowired
	Cage cage;
	@Autowired
	ICaptchaCodesRepository captchaCodesRepository;
	@Value("${captcha.storage-limit}")
	int storageLimit;

	/**
	 * Метод generateCaptcha.
	 * Генерация новой капчи.
	 * Сохранение капчи в базу.
	 * Удаление "капч", созданных ранее данного момента времени.
	 *
	 * @return ResponseEntity<CaptchaResponse>
	 * @throws IOException
	 */
	@Override
	public CaptchaResponse generateCaptcha() throws IOException {
		CaptchaResponse captchaResponse = createResponse();
		this.saveCaptchaInfo(captchaResponse);
		captchaCodesRepository.deleteByTimeBefore(LocalDateTime.now().minusHours(storageLimit));
		return captchaResponse;
	}

	/**
	 * Метод captchaIsExist.
	 * Проверка существования капчи в таблице captcha_codes.
	 *
	 * @param code       код с картинки.
	 * @param secretCode секретный код.
	 * @return true - если капча найдена в таблице captcha_codes.
	 */
	@Override
	public boolean captchaIsExist(String code, String secretCode) {
		return captchaCodesRepository.findByCodeAndSecretCode(code, secretCode).isPresent();
	}

	/**
	 * Метод createResponse.
	 * Создание ответа на запрос GET /api/auth/captcha.
	 *
	 * @return CaptchaResponse.
	 * @throws IOException
	 * @see CaptchaResponse
	 */
	private CaptchaResponse createResponse() throws IOException {
		String captchaToken = cage.getTokenGenerator().next();
		String encodedString = null;
		File file = new File("captcha");
		try (OutputStream os = new FileOutputStream(file, false)) {
			cage.draw(captchaToken, os);
			byte[] fileContent = FileUtils.readFileToByteArray(file);
			encodedString = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return CaptchaResponse.builder()
				.code(captchaToken)
				.secret(UUID.randomUUID().toString())
				.image("data:image/png;base64," + encodedString)
				.build();
	}

	/**
	 * Метод saveCaptchaInfo.
	 * Сохранение данных новой капчи в базу.
	 *
	 * @param captchaResponse ответ на запрос GET /api/auth/captcha
	 * @return результат сохранения в базу.
	 */
	private void saveCaptchaInfo(CaptchaResponse captchaResponse) {
		CaptchaCodes captchaCodes = new CaptchaCodes();
		captchaCodes.setCode(captchaResponse.getCode());
		captchaCodes.setSecretCode(captchaResponse.getSecret());
		captchaCodesRepository.save(captchaCodes);
	}
}
