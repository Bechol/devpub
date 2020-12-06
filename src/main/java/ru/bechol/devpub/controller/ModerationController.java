package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.request.ModerationRequest;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.UserService;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import java.awt.*;
import java.security.Principal;

/**
 * Класс ModerationController.
 * REST контроллер для запросов не через /api/moderation.
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
@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Метод moderatePost.
     * POST запрос /api/moderation
     * @param moderationRequest - тело запроса.
     * @param principal - авторизованный пользователь.
     * @return - Response.
     */
    @Operation(summary = "Действие по модерации", description = "Метод фиксирует действие модератора по посту: " +
            "его утверждение или отклонение. Кроме того, фиксируется moderator_id - идентификатор пользователя, " +
            "который отмодерировал пост. Посты могут модерировать только пользователи с is_moderator = 1")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response moderatePost(@RequestBody ModerationRequest moderationRequest, Principal principal)
            throws PostNotFoundException {
        return postService.moderatePost(moderationRequest, principal);
    }
}
