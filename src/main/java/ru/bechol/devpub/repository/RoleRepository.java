package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Role;

import java.util.Optional;

/**
 * Класс RoleRepository.
 * Реализация слоя доступа к данным для Role.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Role
 * @see ru.bechol.devpub.service.RoleService
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Метод findByName.
     * Поиск роли по наименованию.
     *
     * @param name - наименование роли.
     * @return Role
     */
    Optional<Role> findByName(String name);
}
