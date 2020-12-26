package ru.bechol.devpub.service;

import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Role;

import javax.management.relation.RoleNotFoundException;

/**
 * Интерфейс IRoleService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IRoleService {

	/**
	 * Метод findByName.
	 * Поиск роли по наименованию.
	 *
	 * @param roleName наименование роли.
	 * @return Role роль пользователя.
	 * @throws RoleNotFoundException в случае если роль не найдена по наименованию
	 */
	Role findByName(String roleName) throws RoleNotFoundException;
}
