package ru.bechol.devpub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;

import java.util.List;

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

    /**
     * Метод findAllPostsByDate.
     * Вывод только активных (поле is_active в таблице posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED) постов с датой публикации
     * не позднее текущего момента, на дату, указанную в параметре query.
     *
     * @param pageable
     * @param date     - дата, на которую необходимо вывести посты.
     * @return Page<Post>.
     */
    @Query("from Post as post where to_char(post.time,'YYYY-MM-DD') = :queryDate and " +
            "post.active=true and post.moderationStatus='ACCEPTED' and post.time <= CURRENT_TIMESTAMP")
    Page<Post> findAllPostsByDate(Pageable pageable, @Param("queryDate") String date);

    /**
     * Метод findAllByTag.
     * Вывод только активных (поле is_active в таблице posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED) постов с датой публикации
     * не позднее текущего момента, по тегу, указанному в параметре.
     *
     * @param pageable
     * @param tag      - тег, к которому привязан пост.
     * @return Page<Post>.
     */
    @Query("from Post p where p in (select p from Post as p inner join p.tags as tag with tag.name = :tag group by p)" +
            " and p.active = true AND p.moderationStatus = 'ACCEPTED' AND p.time <= CURRENT_TIMESTAMP")
    Page<Post> findAllByTag(Pageable pageable, @Param("tag") String tag);

    /**
     * Метод findAllByActiveAndUser.
     * Поиск постов по флагу Active и модератору.
     *
     * @param isActive - флаг active
     * @param user     - пользователь.
     * @return List<Post>
     */
    List<Post> findAllByActiveAndUser(boolean isActive, User user);

    /**
     * Метод findByModerationStatusAndActiveTrue.
     * Выборка активных постов по статусу модерации.
     *
     * @param moderationStatus - статус поста.
     * @return List<Post>
     */
    List<Post> findByModerationStatusAndActiveTrue(Post.ModerationStatus moderationStatus);

    /**
     * Метод findByModerationStatusAndActiveTrue.
     * Выборка активных постов, прошедших модерацию у авторизованного модератора.
     *
     * @param moderationStatus - статус поста.
     * @param moderator        - авторизованный модератор.
     * @return List<Post>
     */
    List<Post> findByModeratedByAndModerationStatusAndActiveTrue(User moderator, Post.ModerationStatus moderationStatus);

}
