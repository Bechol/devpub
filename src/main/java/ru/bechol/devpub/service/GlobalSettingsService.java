package ru.bechol.devpub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.models.GlobalSetting;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.GlobalSettingRepository;
import ru.bechol.devpub.response.ErrorResponse;
import ru.bechol.devpub.service.aspect.Trace;
import ru.bechol.devpub.service.enums.SettingValue;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

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
 * @see GlobalSetting
 * @see GlobalSettingRepository
 * @see DefaultController
 */
@Slf4j
@Service
public class GlobalSettingsService {

    @Autowired
    private GlobalSettingRepository globalSettingRepository;
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
    @Trace
    public ResponseEntity<Map<String, Boolean>> createGeneralSettingsMap() {
        Map<String, Boolean> settingsMap = globalSettingRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSetting::getCode,
                        value -> value.getValue().equals(SettingValue.YES.name())));
        if (settingsMap.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(settingsMap);
    }

    /**
     * Метод updateGeneralSettings.
     * Изменение глобальных настроек.
     *
     * @param generalSettingsMap - новые настройки.
     * @param principal          - авторизованный пользователь.
     * @return - 400 - если активный пользователь не существует или не модератор. 200 - после сохранение настроек.
     */
    public ResponseEntity<?> updateGeneralSettings(Map<String, Boolean> generalSettingsMap, Principal principal) {
        User activeUser = userService.findByEmail(principal.getName());
        if (!activeUser.isModerator()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().message(
                    messages.getMessage("er.not.moderator")).build());
        }
        List<GlobalSetting> generalSettings = globalSettingRepository.findAll();
        if (generalSettings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        for (int i = 0; i < generalSettings.size(); i++) {
            GlobalSetting tmpGs = generalSettings.get(i);
            if (generalSettingsMap.get(tmpGs.getCode())) {
                tmpGs.setValue(SettingValue.YES.toString());
            } else {
                tmpGs.setValue(SettingValue.NO.toString());
            }
            generalSettings.set(i, tmpGs);
        }
        globalSettingRepository.saveAll(generalSettings);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод checkSetting.
     * Поиск глобальной настройки и проверка по заданному значению.
     *
     * @param code         - код настройки.
     * @param settingValue - значение настройки.
     * @return - true - если настройка найдена и её значение YES.
     * @throws CodeNotFoundException - когда настройка не найдена по коду.
     */
    public boolean checkSetting(String code, SettingValue settingValue) throws CodeNotFoundException {
        GlobalSetting globalSetting = globalSettingRepository.findByCode(code)
                .orElseThrow(() -> new CodeNotFoundException(messages.getMessage("warning.code.not-found")));
        return globalSetting.getValue().equals(settingValue.toString());
    }
}
