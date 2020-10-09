package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.models.GlobalSettings;
import ru.bechol.devpub.repository.GlobalSettingsRepository;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс GlobalSettingsService.
 * Реализация сервисного слоя для GlobalSettings.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSettings
 * @see GlobalSettingsRepository
 * @see DefaultController
 */
@Service
public class GlobalSettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    /**
     * Метод createGeneralSettingsMap.
     * Подготовка ответа на GET запрос /api/settings.
     *
     * @return ResponseEntity<Map < String, Boolean>>.
     */
    public ResponseEntity<Map<String, Boolean>> createGeneralSettingsMap() {
        Map<String, Boolean> settingsMap = globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSettings::getCode, value -> value.getValue().name().equals(GlobalSettings.SettingValue.YES.name())));
        return ResponseEntity.ok(settingsMap);
    }
}
