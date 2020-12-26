package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.models.GlobalSetting;
import ru.bechol.devpub.repository.IGlobalSettingRepository;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.enums.SettingValue;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс GlobalSettingsService.
 * Реализация сервисного слоя для GlobalSettings.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSetting
 * @see IGlobalSettingRepository
 * @see DefaultController
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class GlobalSettingsService implements IGlobalSettingsService {

	@Autowired
	IGlobalSettingRepository globalSettingRepository;
	@Autowired
	@Qualifier("userService")
	private IUserService userService;
	@Autowired
	Messages messages;

	/**
	 * Метод createGeneralSettingsMap.
	 * Подготовка ответа на GET запрос /api/settings.
	 *
	 * @return ResponseEntity<Map < String, Boolean>>.
	 */
	@Override
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
	 * @param generalSettingsMap новые настройки.
	 * @return 400 - если активный пользователь не существует или не модератор. 200 - после сохранение настроек.
	 */
	@Override
	public ResponseEntity<?> updateGeneralSettings(Map<String, Boolean> generalSettingsMap) {
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
	 * @param code         код настройки.
	 * @param settingValue значение настройки.
	 * @return true - если настройка найдена и её значение YES.
	 * @throws CodeNotFoundException - когда настройка не найдена по коду.
	 */
	@Override
	public boolean checkSetting(String code, SettingValue settingValue) throws CodeNotFoundException {
		GlobalSetting globalSetting = globalSettingRepository.findByCode(code)
				.orElseThrow(() -> new CodeNotFoundException(messages.getMessage("warning.code.not-found")));
		return globalSetting.getValue().equals(settingValue.toString());
	}
}
