package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.service.enums.SettingValue;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

import java.util.Map;

/**
 * Интерфейс IGlobalSettingsService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IGlobalSettingsService {

	/**
	 * Метод createGeneralSettingsMap.
	 * Формирование ответа.
	 *
	 * @return ResponseEntity<Map < String, Boolean>>.
	 */
	ResponseEntity<Map<String, Boolean>> createGeneralSettingsMap();

	/**
	 * Метод updateGeneralSettings.
	 * Изменение глобальных настроек.
	 *
	 * @param generalSettingsMap новые настройки.
	 * @param user               авторизованный пользователь.
	 * @return 400 - если активный пользователь не существует или не модератор. 200 - после сохранение настроек.
	 */
	ResponseEntity<?> updateGeneralSettings(Map<String, Boolean> generalSettingsMap);

	/**
	 * Метод checkSetting.
	 * Поиск глобальной настройки и проверка по заданному значению.
	 *
	 * @param code         код настройки.
	 * @param settingValue значение настройки.
	 * @return true - если настройка найдена и её значение YES.
	 * @throws CodeNotFoundException - когда настройка не найдена по коду.
	 */
	boolean checkSetting(String code, SettingValue settingValue) throws CodeNotFoundException;

}
