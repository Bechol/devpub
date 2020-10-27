package ru.bechol.devpub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Post;

/**
 * Класс PostRepository.
 * Слой доступа к данным Post
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    /**
     * Метод findAllPostsUnsorted.
     * Вывод только активных (поле is_active в таблице posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED) постов с датой публикации
     * не позднее текущего момента.
     *
     * @param pageable
     * @return Page<Post>.
     */
    @Query("SELECT p FROM Post p WHERE " +
            "p.active = true AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_TIMESTAMP")
    Page<Post> findAllPostsUnsorted(Pageable pageable);

    /**
     * Метод findAllPostsByQuery.
     * Вывод только активных (поле is_active в таблице posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED) постов с датой публикации
     * не позднее текущего момента, текст которых содержит строку поискового запроса query.
     *
     * @param pageable
     * @param query    - строка поискового запроса.
     * @return Page<Post>.
     */
    @Query("from Post as post where post.text like concat('%', :query, '%') and " +
            "post.active=true and post.moderationStatus='ACCEPTED' and post.time <= CURRENT_TIMESTAMP")
    Page<Post> findAllPostsByQuery(Pageable pageable, @Param("query") String query);
}
