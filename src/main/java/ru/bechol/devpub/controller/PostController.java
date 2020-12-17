package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.request.*;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.response.dto.PostDto;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.enums.*;
import ru.bechol.devpub.service.exception.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Класс PostController.
 * REST контроллер для обычных запросов не через /api/post.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@Tag(name = "/api/post", description = "Работа с постами")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/post")
public class PostController {

	@Autowired
	PostService postService;
	@Autowired
	VoteService voteService;

	/**
	 * Метод getAllPostsSorted.
	 * GET запрос /api/post.
	 * Метод получения постов со всей сопутствующей информацией для главной страницы и подразделов "Новые",
	 * "Самые обсуждаемые", "Лучшие" и "Старые". Метод выводит посты, отсортированные в соответствии с параметром mode.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода.
	 * @param limit  - количество постов, которое надо вывести.
	 * @param mode   -  режим вывода (сортировка).
	 * @return ResponseEntity<PostsResponse>.
	 */
	@Operation(summary = "Получение постов со всей сопутствующей информацией для главной страницы и подразделов " +
			"\"Новые\", \"Самые обсуждаемые\", \"Лучшие\" и \"Старые\". ", description = "Метод выводит посты, " +
			"отсортированные в соответствии с параметром mode. Выводятся только активные" +
			" (поле is_active в таблице posts равно 1), утверждённые модератором (поле moderation_status " +
			"равно ACCEPTED) посты с датой публикации не позднее текущего момента.")
	@ApiResponses(value = {
			@ApiResponse(description = "Посты найдены", responseCode = "200", content = {
					@Content(schema = @Schema(implementation = PostResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public PostResponse getAllPostsSorted(@RequestParam(defaultValue = "0") int offset,
										  @RequestParam(defaultValue = "20") int limit,
										  @RequestParam String mode) throws EnumValueNotFoundException {
		return postService.findAllPostsSorted(offset, limit, SortMode.fromValue(mode));
	}

	/**
	 * Метод findPostsByTextContainingQuery.
	 * GET запрос /api/post/search.
	 * Метод возвращает посты, соответствующие поисковому запросу - строке query.
	 * В случае, если запрос пустой, метод должен выводить все посты.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода.
	 * @param limit  - количество постов, которое надо вывести.
	 * @param query  -  поисковый запрос.
	 * @return ResponseEntity<PostsResponse>.
	 */
	@Operation(summary = "Возвращает посты, соответствующие поисковому запросу - строке query." +
			" В случае, если запрос пустой, метод должен выводить все посты.", description = "Выводятся только активные" +
			" (поле is_active в таблице posts равно 1), утверждённые модератором (поле moderation_status " +
			"равно ACCEPTED) посты с датой публикации не позднее текущего момента.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = PostResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
	})
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public PostResponse findPostsByTextContainingQuery(@RequestParam(defaultValue = "0") int offset,
													   @RequestParam(defaultValue = "20") int limit,
													   @RequestParam String query) {
		return postService.findPostsByTextContainingQuery(offset, limit, query);
	}

	/**
	 * Метод searchPostsByDate.
	 * GET запрос /api/post/byDate.
	 * Выводит посты за указанную дату, переданную в запросе в параметре date.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода.
	 * @param limit  - количество постов, которое надо вывести.
	 * @param date   -  дата в формате "YYYY-MM-dd"
	 * @return ResponseEntity<PostsResponse>.
	 */
	@Operation(summary = "Посты за указанную дату", description = "Выводятся только активные (поле is_active в таблице " +
			"posts равно 1), утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации " +
			"не позднее текущего момента.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = PostResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
	})
	@GetMapping(value = "/byDate", produces = MediaType.APPLICATION_JSON_VALUE)
	public PostResponse findPostsByDate(@RequestParam(defaultValue = "0") int offset,
										@RequestParam(defaultValue = "20") int limit,
										@Parameter(description = "дата в формате \"YYYY-MM-dd\"") @RequestParam String date) {
		return postService.findPostsByDate(offset, limit, date);
	}

	/**
	 * Метод searchByTag.
	 * GET запрос /api/post/byDate.
	 * Метод выводит список постов, привязанных к тегу, который был передан методу в качестве параметра tag.
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода.
	 * @param limit  - количество постов, которое надо вывести.
	 * @param tag    -  тег, к которому привязан пост.
	 * @return ResponseEntity<PostsResponse>.
	 */
	@Operation(summary = "Посты по тегу", description = "Выводятся только активные (поле is_active в таблице " +
			"posts равно 1), утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации " +
			"не позднее текущего момента.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = PostResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
	})
	@GetMapping(value = "/byTag", produces = MediaType.APPLICATION_JSON_VALUE)
	public PostResponse findByTag(@RequestParam(defaultValue = "0") int offset,
								  @RequestParam(defaultValue = "20") int limit,
								  @RequestParam String tag) {
		return postService.findByTag(offset, limit, tag);
	}

	/**
	 * Метод findMyPosts.
	 * GET запрос /api/post/my.
	 * Метод выводит только те посты, которые создал я (в соответствии с полем user_id в таблице posts базы данных).
	 * Возможны 4 типа вывода (см. ниже описания значений параметра status).
	 *
	 * @param offset - сдвиг от 0 для постраничного вывода.
	 * @param limit  - количество постов, которое надо вывести.
	 * @param status -  статус модерации.
	 * @return ResponseEntity<PostsResponse>.
	 */
	@Operation(summary = "Посты, которые создал авторизованный пользователь")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = PostResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
	public PostResponse findActiveUserPosts(Principal user, @RequestParam(defaultValue = "0") int offset,
											@RequestParam(defaultValue = "0") int limit,
											@RequestParam String status) throws EnumValueNotFoundException {
		return postService.findActiveUserPosts(user, offset, limit, PostStatus.fromValue(status));
	}

	/**
	 * Метод createNewPost.
	 * POST запрос /api/post.
	 * Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок или
	 * текст поста не установлены и/или слишком короткие (короче 3 и 50 символов соответственно), метод должен
	 * выводить ошибку и не добавлять пост.
	 * Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени,
	 * оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать введенное значение.
	 * Пост должен сохраняться со статусом модерации NEW.
	 *
	 * @param postRequest   - json для создания поста.
	 * @param bindingResult - результат валидации данных нового поста.
	 * @param principal     - авторизованный пользователь.
	 * @return ResponseEntity<PostsResponse>.
	 */
	@Operation(summary = "Создание нового поста", description = "В случае, если заголовок или текст поста не установлены " +
			"и/или слишком короткие (короче 3 и 50 символов соответственно), метод должен выводить ошибку " +
			"и не добавлять пост. Время публикации поста также должно проверяться: в случае, если время публикации " +
			"раньше текущего времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо " +
			"устанавливать введенное значение. Пост должен сохраняться со статусом модерации NEW.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Новый пост сохранен",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "ok", description = "пост успешно сохранен",
									value = "{\n\t\"result\": true\n}"),
							@ExampleObject(name = "errors", description = "Значения полей пользовательской формы не прошли валидацию",
									value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
											"\t\t\"title\": \"Заголовок не установлен\"," +
											"\t\t\"text\": \"Текст публикации слишком короткий\"\n}\n}")
					})
					}),
			@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createNewPost(@Valid @RequestBody PostRequest postRequest, BindingResult bindingResult,
										   Principal principal) throws Exception {
		return postService.createNewPost(principal, postRequest, bindingResult);
	}

	/**
	 * Метод showPost.
	 * GET запрос /api/post/{id}.
	 * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
	 * список комментариев и тэгов, привязанных к данному посту. Выводит пост в любом случае, если пост активен
	 * (параметр is_active в базе данных равен 1), принят модератором (параметр moderation_status равен ACCEPTED) и
	 * время его публикации (поле timestamp) равно текущему времени или меньше его.
	 *
	 * @param postId    - id поста.
	 * @param principal - авторизованный пользователь.
	 * @return ResponseEntity<Response>.
	 */
	@Operation(summary = "Данные конкретного поста", description = "Метод выводит данные конкретного поста для " +
			"отображения на странице поста, в том числе, список комментариев и тэгов, привязанных к данному посту. " +
			"Выводит пост в любом случае, если пост активен (параметр is_active в базе данных равен 1), принят " +
			"модератором (параметр moderation_status равен ACCEPTED) и время его публикации (поле timestamp) " +
			"равно текущему времени или меньше его.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = PostDto.class))
			}),
			@ApiResponse(responseCode = "400", description = "Пост не найден",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PostDto showPost(@PathVariable(name = "id") long postId, Principal principal) throws PostNotFoundException {
		return postService.showPost(postId, principal);
	}

	/**
	 * Метод editPost.
	 * PUT запрос /api/post/{id}.
	 * Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму публикации.
	 *
	 * @param editPostRequest - тело запроса.
	 * @param bindingResult   - результаты валидации данных пользовательской формы.
	 * @param postId          - id поста.
	 * @param principal       - авторизованный пользователь.
	 * @return ResponseEntity<Response>.
	 */
	@Operation(summary = "Редактирование поста", description = "Метод изменяет данные поста с идентификатором ID на те, " +
			"которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены " +
			"и/или слишком короткие (короче 3 и 50 символов соответственно), метод должен выводить ошибку и не изменять" +
			" пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего " +
			"времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать " +
			"указанное значение. Пост должен сохраняться со статусом модерации NEW, если его изменил автор, и статус " +
			"модерации не должен изменяться, если его изменил модератор.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Новый пост сохранен",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "ok", description = "пост успешно сохранен",
									value = "{\n\t\"result\": true\n}"),
							@ExampleObject(name = "errors", description = "Значения полей пользовательской формы не прошли валидацию",
									value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
											"\t\t\"title\": \"Заголовок не установлен\"," +
											"\t\t\"text\": \"Текст публикации слишком короткий\"\n}\n}")
					})
					}),
			@ApiResponse(responseCode = "400", description = "Пост не найден",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> editPost(@Valid @RequestBody PostRequest editPostRequest, BindingResult bindingResult,
									  @PathVariable("id") long postId, Principal principal) throws Exception {
		return postService.editPost(editPostRequest, postId, principal, bindingResult);
	}


	/**
	 * Метод postsOnModeration
	 * GET запрос /api/post/moderation
	 * Метод выводит все посты, которые требуют модерационных действий (которые нужно утвердить или отклонить)
	 * или над которыми мною были совершены модерационные действия: которые я отклонил или утвердил
	 * (это определяется полями moderation_status и moderator_id в таблице posts базы данных).
	 *
	 * @param offset    - сдвиг от 0 для постраничного вывода.
	 * @param limit     - количество постов, которое надо вывести.
	 * @param status    -  статус модерации.
	 * @param principal - авторизованный пользователь
	 * @return - PostResponse.
	 */
	@Operation(summary = "Посты, которые требуют модерационных действий", description = "Метод выводит все посты, " +
			"которые требуют модерационных действий (которые нужно утвердить или отклонить) или над которыми мною " +
			"были совершены модерационные действия: которые я отклонил или утвердил (это определяется полями " +
			"moderation_status и moderator_id в таблице posts базы данных).")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = PostResponse.class))
			}),
			@ApiResponse(responseCode = "400", description = "Ошибка в данных запроса",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@GetMapping(value = "/moderation", produces = MediaType.APPLICATION_JSON_VALUE)
	public PostResponse postsOnModeration(@RequestParam int offset, @RequestParam int limit,
										  @RequestParam String status, Principal principal)
			throws EnumValueNotFoundException {
		return postService.findPostsOnModeration(principal, offset, limit, status);
	}

	/**
	 * Метод likePost
	 * POST запрос /api/post/like
	 * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя.
	 * В случае повторного лайка возвращает {result: false}. Если до этого этот же пользователь поставил на этот
	 * же пост дизлайк, этот дизлайк должен быть заменен на лайк в базе данных.
	 *
	 * @param postIdRequest - id поста для лайка.
	 * @param principal     - авторизованный пользователь
	 * @return - Response.
	 */
	@Operation(summary = "Лайк текущего авторизованного пользователя", description = "В случае повторного лайка " +
			"возвращает {result: false}. Если до этого этот же пользователь поставил на этот же пост дизлайк, этот " +
			"дизлайк должен быть заменен на лайк в базе данных.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "like", description = "лайк произошел",
									value = "{\n\t\"result\": true\n}"),
							@ExampleObject(name = "not like", description = "лайк не произошел",
									value = "{\n\t\"result\": false\n}")
					})
					}),
			@ApiResponse(responseCode = "400", description = "Пост не найден",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@PostMapping(value = "/like", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> likePost(@RequestBody PostIdRequest postIdRequest, Principal principal)
			throws PostNotFoundException {
		return voteService.vote(postIdRequest, principal, 1);
	}

	/**
	 * Метод dislikePost
	 * POST запрос /api/post/dislike
	 * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя.
	 * В случае повторного дизлайка возвращает {result: false}.
	 * Если до этого этот же пользователь поставил на этот же пост лайк,
	 * этот лайк должен заменен на дизлайк в базе данных.
	 *
	 * @param postIdRequest - id поста для лайка.
	 * @param principal     - авторизованный пользователь
	 * @return - Response.
	 */
	@Operation(summary = "Дизлайк текущего авторизованного пользователя", description = "В случае повторного дизлайка " +
			"возвращает {result: false}. Если до этого этот же пользователь поставил на этот же пост лайк, этот " +
			"лайк должен быть заменен на дизлайк в базе данных.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "dislike", description = "дизлайк произошел",
									value = "{\n\t\"result\": true\n}"),
							@ExampleObject(name = "not dislike", description = "дизлайк не произошел",
									value = "{\n\t\"result\": false\n}")
					})
					}),
			@ApiResponse(responseCode = "400", description = "Пост не найден",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))})
	})
	@PostMapping(value = "/dislike", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> dislikePost(@RequestBody PostIdRequest postIdRequest, Principal principal)
			throws PostNotFoundException {
		return voteService.vote(postIdRequest, principal, - 1);
	}
}
