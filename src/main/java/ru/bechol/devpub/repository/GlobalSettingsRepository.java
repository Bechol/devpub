package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.GlobalSettings;

/**
 * Класс GlobalSettingsRepository.
 * Реализация слоя доступа к данным для GlobalSettings.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSettings
 * @see ru.bechol.devpub.service.GlobalSettingsService
 */
@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
}
