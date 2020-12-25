package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

/**
 * Класс Role.
 * Доменный объект, представляющий роль пользователя.
 *
 * @author Oleg Bech.
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.RoleRepository
 * @see ru.bechol.devpub.service.RoleService
 * @see ru.bechol.devpub.controller.ApiAuthController
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {

	@Column(name = "role_name", nullable = false)
	String name;

	@JsonBackReference
	@ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
	List<User> users;

	@Override
	public String getAuthority() {
		return name;
	}
}
