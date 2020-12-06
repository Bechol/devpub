package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.request.CommentRequest;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.response.ErrorResponse;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.CommentService;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

/**
 * Класс CommentController.
 * REST контроллер для обработки всех запросов через /api/comment
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CommentService
 */
@Tag(name = "/api/comment", description = "Работа с комментариями")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Метод commentPost.
     * POST запрос /api/comment
     * Добавление комментария к посту.
     *
     * @param commentRequest - тело запроса.
     * @param bindingResult  - результаты валидации тела запроса.
     * @param principal      - авторизованный пользователь.
     * @return ResponseEntity<?>.
     */

    @Operation(summary = "Добвление комментария к посту", description = "Должны проверяться все три параметра. " +
            "Если параметры parent_id и/или post_id неверные (соответствующие комментарий и/или пост не существуют)," +
            " должна выдаваться ошибка 400. В случае, если текст комментария отсутствует (пустой) или слишком" +
            " короткий, необходимо выдавать ошибку в JSON-формате.")
    @ApiResponses(value = {
            @ApiResponse(description = "Комментарий создан", responseCode = "200", content = {
                    @Content(examples = {
                            @ExampleObject(description = "успешный ответ", value = "{\n\t\"id\": 345\n}")},
                            schema = @Schema(implementation = Map.class))
            }),
            @ApiResponse(responseCode = "400", description = "Ошибки при выполнении запроса",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                            @ExampleObject(
                                    value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
                                            "\t\t\"text\": \"Текст комментария не задан или слишком короткий\"\n}\n}")
                    }, schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(hidden = true))})
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(name = "v1", description = "Добавление комментария к самому посту",
                                    value = "{\n\t\"parent_id\": \"\",\n" +
                                            "\t\"post_id\": \"21\"\n" +
                                            "\t\"text\":\"текст комментария\"\n}"
                            ),
                            @ExampleObject(name = "v2", description = "Добавление комментария к другому комментарию",
                                    value = "{\n\t\"parent_id\": \"31\",\n" +
                                            "\t\"post_id\": \"21\"\n" +
                                            "\t\"text\":\"текст комментария\"\n}"
                            )
                    }, schema = @Schema(implementation = CommentRequest.class)
            )
            }
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentPost(@Valid @RequestBody CommentRequest commentRequest, BindingResult bindingResult,
                                         Principal principal) throws PostNotFoundException {
        return commentService.addComment(commentRequest, principal, bindingResult);
    }
}
