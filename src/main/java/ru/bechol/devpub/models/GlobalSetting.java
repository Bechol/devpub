package ru.bechol.devpub.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bechol.devpub.repository.IGlobalSettingRepository;

import javax.persistence.*;

/**
 * Класс GlobalSettings.
 * Доменный объект, представляющий глобальную настройку блога.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see IGlobalSettingRepository
 * @see ru.bechol.devpub.service.IGlobalSettingsService
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "global_settings")
public class GlobalSetting extends BaseEntity {

	@Column(nullable = false)
	String code;
	@Column(nullable = false)
	String name;
	@Column(nullable = false)
	String value;
}
