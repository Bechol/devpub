package ru.bechol.devpub.models;

import lombok.Data;
import ru.bechol.devpub.repository.GlobalSettingRepository;

import javax.persistence.*;

/**
 * Класс GlobalSettings.
 * Реализация сущности глобальных настроек блога.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSettingRepository
 * @see ru.bechol.devpub.service.GlobalSettingsService
 */
@Data
@Entity
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String value;
}
