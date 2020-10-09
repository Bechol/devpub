package ru.bechol.devpub.models;

import lombok.Data;

import javax.persistence.*;

/**
 * Класс GlobalSettings.
 * Реализация сущности глобальных настроек блога.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.GlobalSettingsRepository
 * @see ru.bechol.devpub.service.GlobalSettingsService
 */
@Data
@Entity
@Table(name = "global_settings")
public class GlobalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettingValue value;

    public enum SettingValue {
        YES, NO;
    }
}
