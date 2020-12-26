package ru.bechol.devpub.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.*;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Класс IVoteRepository.
 * Реализация слоя доступа к данным для Vote.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see Vote
 * @see ru.bechol.devpub.service.IVoteService
 */
@Repository
public interface IVoteRepository extends JpaRepository<Vote, Long> {

	/**
	 * Метод deleteByPostAndUserAndValue.
	 * Удаление лайка/дизлайка
	 *
	 * @param post  пост
	 * @param user  авторизованный пользователь.
	 * @param value 1:like, -1:dislike
	 */
	@Transactional
	@Modifying
	@Query("delete from Vote v where v.user=:user and v.post=:post and v.value=:value ")
	void deleteByPostAndUserAndValue(@Param("post") Post post, @Param("user") User user, @Param("value") int value);

	/**
	 * Метод findByPostIn.
	 * Выборка всех лайков и дизлайков в коллекции постов.
	 *
	 * @param posts - коллекция постов для поиска.
	 * @return List<Vote>
	 */
	List<Vote> findByPostIn(List<Post> posts);
}
