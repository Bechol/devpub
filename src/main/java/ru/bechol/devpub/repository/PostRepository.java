package ru.bechol.devpub.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bechol.devpub.models.*;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
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
     * Метод findByModerationStatusAndActiveTrueAndTimeBefore.
     * Вывод только активных (поле is_active в таблице posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED) постов с датой публикации
     * не позднее текущего момента.
     *
     * @param moderationStatus - статус модерации
     * @param time - время, ранее которого посты были созданы
     * @param pageable - настройки пагинации
     * @return Page<Post>.
     * @see ru.bechol.devpub.service.PostService
     */
    Page<Post> findByModerationStatusAndActiveTrueAndTimeBefore(String moderationStatus,
                                                                LocalDateTime time, Pageable pageable);

    /**
     * Метод findByModerationStatusAndActiveTrueAndTimeBeforeAndTextContainingIgnoreCase.
     * Вывод только активных (поле is_active в таблице posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED) постов с датой публикации
     * не позднее текущего момента, текст которых содержит строку поискового запроса query.
     *
     * @param moderationStatus - статус модерации
     * @param time - время, ранее которого посты были созданы
     * @param pageable - настройки пагинации
     * @param query    - строка поискового запроса.
     * @return Page<Post>.
     */
    Page<Post> findByModerationStatusAndActiveTrueAndTimeBeforeAndTextContainingIgnoreCase(
            String moderationStatus, LocalDateTime time, String query, Pageable pageable);

    /**
     * Метод findByDate.
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
    Page<Post> findByDate(Pageable pageable, @Param("queryDate") String date);

    /**
     * Метод findByTag.
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
    Page<Post> findByTag(Pageable pageable, @Param("tag") String tag);

    /**
     * Метод findAllByActiveAndUser.
     * Поиск постов по флагу Active и модератору.
     *
     * @param user - пользователь.
     * @return List<Post>
     */
    List<Post> findByUserAndActiveTrue(User user);

    /**
     * Метод findByModerationStatusAndActiveTrue.
     * Выборка активных постов по статусу модерации.
     *
     * @param moderationStatus - статус поста.
     * @return List<Post>
     */
    Page<Post> findByModerationStatusAndActiveTrue(String moderationStatus, Pageable pageable);

    /**
     * Метод findByModerationStatusAndActiveTrue.
     * Выборка активных постов, прошедших модерацию у авторизованного модератора.
     *
     * @param moderationStatus - статус поста.
     * @param moderator        - авторизованный модератор.
     * @return List<Post>
     */
    Page<Post> findByModeratedByAndModerationStatusAndActiveTrue(User moderator, String moderationStatus,
                                                                 Pageable pageable);

	/**
	 * Метод findAllYearsWithPosts.
	 * Подготовка списка "годов", за которые была создана хотя бы одна публикация.
	 *
	 * @return List<String>.
	 */
	@Query("select year(p.time) from Post p group by year(p.time)")
	List<String> findAllYearsWithPosts();

	/**
	 * Метод agregatePostsByYear.
	 * Агрегация постов по дате за год. В ответ включена дата и количество постов с этой датой.
	 *
	 * @param year - год для выборки.
	 * @return List<Tuple>
	 */
	@Query("select to_char(p.time, 'YYYY-MM-DD') as date, count(p) as count from Post p where year(p.time) = :year group by date")
	List<Tuple> agregatePostsByYear(@Param("year") Integer year);

	/**
	 * Метод findBestPosts.
	 * Запрос на выборку всех активных, опубликованных ранее текущей даты постов с
	 * сортировкой по убыванию количества лайков.
	 *
	 * @param pageable пагинация.
	 * @return Page<Post>
	 */
	@Query("select post from Post post " +
			"left join post.votes post_votes " +
			"where post_votes.value = 1 " +
			"and post.active = true " +
			"and post.moderationStatus = 'ACCEPTED' " +
			"and post.time <= CURRENT_TIMESTAMP " +
			"group by post " +
			"order by count(post_votes) desc")
	Page<Post> findBestPosts(Pageable pageable);

	/**
	 * Метод findPopularPosts.
	 * Запрос на выборку всех активных, опубликованных ранее текущей даты постов с
	 * сортировкой по убыванию количества комментариев.
	 *
	 * @param pageable пагинация.
	 * @return Page<Post>
	 */
	@Query("select post from Post post " +
			"left join post.comments post_comments " +
			"where post.active = true " +
			"and post.moderationStatus = 'ACCEPTED' " +
			"and post.time <= CURRENT_TIMESTAMP " +
			"group by post " +
			"order by count(post_comments) desc")
	Page<Post> findPopularPosts(Pageable pageable);
}
