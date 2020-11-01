package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.response.GeneralInfoResponse;
import ru.bechol.devpub.service.GlobalSettingsService;

import java.security.Principal;
import java.util.Map;

/**
 * Класс DefaultController.
 * REST контроллер для обычных запросов не через API (главная страница - /, в частности)
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSettingsService
 */
@RestController
@RequestMapping("/api")
public class DefaultController {

    @Autowired
    private GeneralInfoResponse generalInfoResponse;

    @Autowired
    private GlobalSettingsService globalSettingsService;

    /**
     * Метод getGeneralInfo.
     * GET запрос /api/init.
     *
     * @return общая информация о блоге в json формате.
     */
    @GetMapping("/init")
    private ResponseEntity<GeneralInfoResponse> getGeneralInfo() {
        return ResponseEntity.ok(generalInfoResponse);
    }

    /**
     * Метод getGeneralSettings.
     * GET запрос /api/settings
     *
     * @return глобальные настройки блога из таблицы global_settings.
     */
    @GetMapping("/settings")
    private ResponseEntity<Map<String, Boolean>> getGeneralSettings() {
        return globalSettingsService.createGeneralSettingsMap();
    }

    /**
     * Метод updateGeneralSettings.
     * PUT запрос /api/settings
     * Метод записывает глобальные настройки блога в таблицу global_settings,
     * если запрашивающий пользователь авторизован и является модератором.
     *
     * @return глобальные настройки блога из таблицы global_settings.
     */
    @PutMapping("/settings")
    private ResponseEntity updateGeneralSettings(@RequestBody Map<String, Boolean> settings, Principal principal) {
        return globalSettingsService.updateGeneralSettings(settings, principal);
    }
}
