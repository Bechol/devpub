package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.GlobalSetting;

import java.util.Optional;

/**
 * Класс GlobalSettingsRepository.
 * Реализация слоя доступа к данным для GlobalSettings.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSetting
 * @see ru.bechol.devpub.service.IGlobalSettingsService
 */
@Repository
public interface IGlobalSettingRepository extends JpaRepository<GlobalSetting, Long> {

	/**
	 * Метод findByCode.
	 * Поиск по полю code.
	 *
	 * @param code - код настройки.
	 * @return - Optional<GlobalSetting>.
	 */
	Optional<GlobalSetting> findByCode(String code);
}
