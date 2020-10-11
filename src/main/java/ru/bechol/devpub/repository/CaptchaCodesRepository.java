package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.CaptchaCodes;

import java.time.LocalDateTime;
import java.util.Optional;

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
     * Метод findByCodeAndSecretCode.
     * Поиск по коду и секретному коду капчи.
     * @param code - код на картинке капчи.
     * @param secretCode - секретный код.
     * @return Optional<CaptchaCodes>.
     */
    Optional<CaptchaCodes> findByCodeAndSecretCode(String code, String secretCode);

    /**
     * Метод deleteOld.
     * Удаление из таблицы captcha_codes ранее созданных записей.
     *
     * @param storageTimeLimit - момент времени, созданные ранее которого записи считаются устаревшими.
     */
    @Query(value = "DELETE FROM captcha_codes c where c.time < :storageTimeLimit", nativeQuery = true)
    void deleteOld(@Param("storageTimeLimit") LocalDateTime storageTimeLimit);

}