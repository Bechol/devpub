package ru.bechol.devpub.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Абстрактный класс BaseEntity.
 * Базовый класс для всех сущностей в доменной модели.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	/**
	 * Поле id.
	 * Идентификатор
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
}
