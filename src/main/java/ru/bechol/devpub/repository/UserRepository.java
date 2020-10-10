package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.User;

import java.util.Optional;

/**
 * Класс UserRepository.
 * Реализация слоя доступа к данным для User.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see User
 * @see ru.bechol.devpub.service.UserService
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Метод findByEmail.
     * Поиск пользователя по email.
     *
     * @param email - email для поиска.
     * @return Optional<User>.
     */
    Optional<User> findByEmail(String email);
}
