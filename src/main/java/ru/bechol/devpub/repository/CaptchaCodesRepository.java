package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.CaptchaCodes;

import java.time.LocalDateTime;

/**
 * Класс CaptchaCodesRepository.
 * Реализация слоя доступа к данным для CaptchaCodes.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CaptchaCodes
 * @see ru.bechol.devpub.service.CaptchaCodesService
 */
@Repository
public interface CaptchaCodesRepository extends JpaRepository<CaptchaCodes, Long> {

    /**
     * Метод deleteOld.
     * Удаление из таблицы captcha_codes ранее созданных записей.
     *
     * @param storageTimeLimit - момент времени, созданные ранее которого записи считаются устаревшими.
     */
    @Query(value = "DELETE FROM captcha_codes c where c.time < :storageTimeLimit", nativeQuery = true)
    void deleteOld(@Param("storageTimeLimit") LocalDateTime storageTimeLimit);

}
