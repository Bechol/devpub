package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Role;
import ru.bechol.devpub.repository.RoleRepository;

import javax.management.relation.RoleNotFoundException;

/**
 * Класс RoleService.
 * Сервсиный слой для Role.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see Role
 * @see RoleRepository
 * @see Messages
 */
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private Messages messages;

    /**
     * Метод findByName.
     * Поиск роли по наименованию.
     * @param roleName - наименование роли.
     * @return Role
     * @throws RoleNotFoundException - в случае если роль не найдена по наименованию
     */
    public Role findByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(messages.getMessage("warning.role.not-found")));
    }
}
