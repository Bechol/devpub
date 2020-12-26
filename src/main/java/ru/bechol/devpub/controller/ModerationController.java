package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.ModerationRequest;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.*;

/**
 * Класс ModerationController.
 * REST контроллер.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Tag(name = "/api/moderation", description = "Модерация постов")
@ApiResponses(value = {
		@ApiResponse(description = "Модерация выполнена", responseCode = "200", content = {
				@Content(schema = @Schema(implementation = Response.class))
		}),
		@ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
				content = {@Content(schema = @Schema(hidden = true))}),
		@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
				content = {@Content(schema = @Schema(hidden = true))})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

	@Autowired
	@Qualifier("postService")
	IPostService postService;
	@Autowired
	@Qualifier("userService")
	IUserService userService;

	/**
	 * Метод moderatePost.
	 * POST запрос /api/moderation
	 *
	 * @param moderationRequest тело запроса.
	 * @param principal         авторизованный пользователь.
	 * @return - Response.
	 */
	@Operation(summary = "Действие по модерации", description = "Метод фиксирует действие модератора по посту: " +
			"его утверждение или отклонение. Кроме того, фиксируется moderator_id - идентификатор пользователя, " +
			"который отмодерировал пост. Посты могут модерировать только пользователи с is_moderator = 1")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<?> moderatePost(@RequestBody ModerationRequest moderationRequest, @AuthenticationPrincipal User user)
			throws Exception {
		return postService.moderatePost(moderationRequest, user);
	}
}
