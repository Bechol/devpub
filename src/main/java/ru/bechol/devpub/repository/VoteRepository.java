package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Vote;

/**
 * Класс RoleRepository.
 * Реализация слоя доступа к данным для Vote.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Vote
 * @see ru.bechol.devpub.service.VoteService
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {


}
