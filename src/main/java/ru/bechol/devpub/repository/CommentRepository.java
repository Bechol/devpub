package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Comment;

/**
 * Класс CommentRepository.
 * Слой доступа к данным для Comment.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Comment
 * @see ru.bechol.devpub.service.CommentService
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
