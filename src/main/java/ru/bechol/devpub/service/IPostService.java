package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.request.*;
import ru.bechol.devpub.response.*;
import ru.bechol.devpub.response.dto.PostDto;
import ru.bechol.devpub.service.enums.*;
import ru.bechol.devpub.service.exception.*;

import java.util.List;

/**
 * Интерфейс IGlobalSettingsService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IPostService {

	/**
	 * Метод getPostsWithSortMode.
	 * Формирует список постов для отображения в зависимости от заданного режима сортировки.
	 *
	 * @param offset   сдвиг от 0 для постраничного вывода.
	 * @param limit    количество постов, которое надо вывести.
	 * @param sortMode режим сортировки.
	 * @return PostResponse
	 */
	PostResponse getPostsWithSortMode(int offset, int limit, SortMode sortMode);

	/**
	 * Метод findPostsByTextContainingQuery.
	 * Метод находит все посты, текст которых содержит строку query.
	 *
	 * @param offset сдвиг от 0 для постраничного вывода.
	 * @param limit  количество постов, которое надо вывести.
	 * @param query  поисковый запрос.
	 * @return PostResponse
	 */
	PostResponse findPostsByTextContainingQuery(int offset, int limit, String query);

	/**
	 * Метод findPostsByDate.
	 * Выводит посты за указанную дату, переданную в запросе в параметре date.
	 *
	 * @param offset сдвиг от 0 для постраничного вывода
	 * @param limit  количество постов, которое надо вывести
	 * @param date   дата, за которую необходимо отобрать посты.
	 * @return - PostResponse.
	 */
	PostResponse findPostsByDate(int offset, int limit, String date);

	/**
	 * Метод findByTag.
	 * Метод выводит список постов, привязанных к тегу, который был передан методу в качестве параметра tag.
	 *
	 * @param offset сдвиг от 0 для постраничного вывода
	 * @param limit  количество постов, которое надо вывести
	 * @param tag    тег, к которому привязаны посты.
	 * @return PostResponse.
	 */
	PostResponse findByTag(int offset, int limit, String tag);

	/**
	 * Метод findActiveUserPosts.
	 * Метод формирует ответ на GET запрос /api/post/my.
	 *
	 * @param offset     сдвиг от 0 для постраничного вывода
	 * @param limit      количество постов, которое надо вывести
	 * @param postStatus статус модерации.
	 * @return PostResponse.
	 */
	PostResponse findActiveUserPosts(User user, int offset, int limit, PostStatus postStatus);

	/**
	 * Метод createNewPost.
	 * Создание поста.
	 *
	 * @param user          авторизованный пользователь.
	 * @param postRequest   данные нового поста.
	 * @param bindingResult результаты валидации данных нового поста.
	 * @return ResponseEntity<Response < ?>>.
	 */
	ResponseEntity<?> createNewPost(User user, PostRequest postRequest, BindingResult bindingResult) throws Exception;

	/**
	 * Метод savePost.
	 * Сохранение поста и при необходимости отправка уведомления модератору.
	 *
	 * @param post пост, который необходимо сохранить,
	 */
	@Async("asyncExecutor")
	void savePost(Post post);

	/**
	 * Метод showPost.
	 * Вывод поста по индентификатору.
	 *
	 * @param postId id поста.
	 * @param user   авторизованный пользователь.
	 * @return ResponseEntity<PostDto>
	 */
	PostDto showPost(long postId, User user) throws PostNotFoundException;

	/**
	 * Метод editPost.
	 * Редактирование поста.
	 *
	 * @param editPostRequest тело запроса.
	 * @param postId          id поста.
	 * @return ResponseEntity<?>.
	 */
	ResponseEntity<?> editPost(PostRequest editPostRequest, long postId, BindingResult bindingResult)
			throws Exception;

	/**
	 * Метод findPostsOnModeration.
	 * Формирование ответа для запроса GET /api/post/moderation
	 *
	 * @param user   авторизованный пользователь.
	 * @param offset сдвиг от 0 для постраничного вывода
	 * @param limit  количество постов, которое надо вывести
	 * @param status статус модерации.
	 * @return PostResponse.
	 */
	PostResponse findPostsOnModeration(User user, int offset, int limit, String status)
			throws EnumValueNotFoundException;

	/**
	 * Метод moderatePost.
	 * Модерация поста.
	 *
	 * @param moderationRequest тело запроса.
	 * @param user              авторизованный пользователь.
	 * @return Response.
	 */
	Response<?> moderatePost(ModerationRequest moderationRequest, User user)
			throws PostNotFoundException;

	/**
	 * Метод createCalendarData.
	 * Метод выводит количества публикаций на каждую дату переданного в параметре year года или текущего года,
	 * если параметр year не задан. В параметре years всегда возвращается список всех годов, з
	 * а которые была хотя бы одна публикация, в порядке возрастания.
	 *
	 * @param year год.
	 * @return CalendarResponse.
	 */
	CalendarResponse createCalendarData(String year);

	/**
	 * Метод findPostsByStatus.
	 * Вывод количества постов по статусу.
	 *
	 * @param moderationStatus статус модерации.
	 * @return количество постов.
	 */
	long findPostsByStatus(ModerationStatus moderationStatus);

	/**
	 * Метод findAll.
	 * Поиск/вывод всех постов.
	 *
	 * @return коллекция, найденных постов.
	 */
	List<Post> findAll();

	/**
	 * Метод findById.
	 * Поиск поста по id.
	 *
	 * @param postId id искомого поста.
	 * @return найденный пост.
	 * @throws PostNotFoundException - исключение, если пост не найден.
	 */
	Post findById(long postId) throws PostNotFoundException;


}
