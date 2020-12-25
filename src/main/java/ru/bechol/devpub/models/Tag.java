package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

/**
 * Класс Tag.
 * Доменный объект, представляющий тег поста.
 *
 * @author Oleg Bech.
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.TagRepository
 * @see ru.bechol.devpub.service.TagService
 * @see ru.bechol.devpub.service.PostService
 * @see ru.bechol.devpub.controller.TagController
 * @see ru.bechol.devpub.controller.PostController
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tags")
@NoArgsConstructor
public class Tag extends BaseEntity {

	@Column(nullable = false)
	String name;

	@JsonBackReference
	@ManyToMany(mappedBy = "tags")
	Set<Post> posts;

	public Tag(String name) {
		this.name = name;
	}
}
