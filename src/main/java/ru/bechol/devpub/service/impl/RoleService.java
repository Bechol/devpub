package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.models.Role;
import ru.bechol.devpub.repository.IRoleRepository;
import ru.bechol.devpub.service.*;

import javax.management.relation.RoleNotFoundException;

/**
 * Класс RoleService.
 * Сервсиный слой для Role.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see Role
 * @see IRoleRepository
 * @see Messages
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class RoleService implements IRoleService {

	@Autowired
	IRoleRepository roleRepository;
	@Autowired
	Messages messages;

	/**
	 * Метод findByName.
	 * Поиск роли по наименованию.
	 *
	 * @param roleName наименование роли.
	 * @return Role
	 * @throws RoleNotFoundException в случае если роль не найдена по наименованию
	 */
	@Override
	public Role findByName(String roleName) throws RoleNotFoundException {
		return roleRepository.findByName(roleName)
				.orElseThrow(() -> new RoleNotFoundException(messages.getMessage("warning.role.not-found")));
	}
}
