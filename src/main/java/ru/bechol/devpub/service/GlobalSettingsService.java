package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.models.GlobalSettings;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.GlobalSettingsRepository;
import ru.bechol.devpub.response.ErrorResponse;

import java.security.Principal;
import java.util.List;
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
    @Autowired
    private UserService userService;
    @Autowired
    private Messages messages;

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

    /**
     * Метод updateGeneralSettings.
     * Изменение глобальных настроек.
     * @param generalSettingsMap - новые настройки.
     * @param principal - авторизованный пользователь.
     * @return - 400 - если активный пользователь не существует или не модератор. 200 - после сохранение настроек.
     */
    public ResponseEntity updateGeneralSettings(Map<String, Boolean> generalSettingsMap, Principal principal) {
        User activeUser = userService.findByEmail(principal.getName()).orElse(null);
        if (activeUser == null || !activeUser.isModerator()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().message(
                    messages.getMessage("er.not.moderator")).build());
        }
        List<GlobalSettings> generalSettings = globalSettingsRepository.findAll();
        for (int i = 0; i < generalSettings.size(); i++) {
            GlobalSettings tmpGs = generalSettings.get(i);
            if (generalSettingsMap.get(tmpGs.getCode())) {
                tmpGs.setValue(GlobalSettings.SettingValue.YES);
            } else {
                tmpGs.setValue(GlobalSettings.SettingValue.NO);
            }
            generalSettings.set(i, tmpGs);
        };
        globalSettingsRepository.saveAll(generalSettings);
        return ResponseEntity.ok().build();
    }
}
