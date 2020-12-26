package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Comment;
import ru.bechol.devpub.service.impl.CommentService;

/**
 * Класс ICommentRepository.
 * Слой доступа к данным для Comment.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Comment
 * @see CommentService
 */
@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
}
