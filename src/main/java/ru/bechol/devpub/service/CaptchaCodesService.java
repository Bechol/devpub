package ru.bechol.devpub.service;

import com.github.cage.Cage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.models.CaptchaCodes;
import ru.bechol.devpub.repository.CaptchaCodesRepository;
import ru.bechol.devpub.response.CaptchaResponse;

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
 * @see CaptchaCodesRepository
 * @see DefaultController
 */
@Slf4j
@Service
public class CaptchaCodesService {

    @Autowired
    private Messages messages;
    @Autowired
    private Cage cage;
    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;
    @Value("${captcha.storage-limit}")
    private int storageLimit;

    /**
     * Метод generateCaptcha.
     * Генерация новой капчи.
     * Сохранение капчи в базу.
     * Публикация события удаления "капч", созданных ранее данного момента времени.
     *
     * @return ResponseEntity<CaptchaResponse>
     * @throws IOException
     */
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
     * @param code       - код с картинки.
     * @param secretCode - секретный код.
     * @return true - если капча найдена в таблице captcha_codes.
     */
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
