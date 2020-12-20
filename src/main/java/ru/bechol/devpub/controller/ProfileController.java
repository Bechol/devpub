package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.ProfileService;

import java.security.Principal;
import java.util.Map;

/**
 * Класс ProfileController.
 * REST контроллер для запросов /api/profile.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Tag(name = "/api/profile", description = "Работа с профилем пользователя")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Новый пост сохранен",
                content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(name = "ok", description = "Данные отправлены верно",
                                value = "{\n\t\"result\": true\n}"),
                        @ExampleObject(name = "errors", description = "Значения полей пользовательской формы " +
                                "не прошли валидацию",
                                value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
                                        "\t\t\"email\": \"Этот e-mail уже зарегистрирован\"," +
                                        "\t\t\"photo\": \"Фото слишком большое, нужно не более 5 Мб\"," +
                                        "\t\t\"name\": \"Имя указано неверно\"," +
                                        "\t\t\"password\": \"Пароль короче 6-ти символов\"\n}\n}")
                }, schema = @Schema(implementation = Response.class))
                }),
        @ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
                content = {@Content(schema = @Schema(hidden = true))}),
        @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                content = {@Content(schema = @Schema(hidden = true))})
})
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Метод changeUserNameEmailAndPassword.
     * POST запрос /api/profile/my
     * Изменение email, имени или пароля пользователя. Удаление аватара.
     *
     * @param editProfileParametersMap - мапа с параметрами из тела запроса.
     * @param principal                - авотризованный пользователь.
     * @return - Response.
     */
    @Operation(summary = "Редактирование профиля", description = "Метод обрабатывает информацию, введённую " +
            "пользователем в форму редактирования своего профиля. Если пароль не введён, его не нужно изменять. " +
            "Если введён, должна проверяться его корректность: достаточная длина. Одинаковость паролей, " +
            "введённых в двух полях, проверяется на frontend - на сервере проверка не требуется.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(name = "v1", description = "Запрос без изменения пароля и фотографии",
                                    value = "{\n\t\"name\": \"Sendel\",\n\t\"email\": \"sndl@mail.ru\"\n}"
                            ),
                            @ExampleObject(name = "v2",
                                    description = "Запрос c изменением пароля и без изменения фотографии",
                                    value = "{\n\t\"name\": \"Sendel\",\n\t\"email\": \"sndl@mail.ru\"," +
                                            "\n\t\"password\": \"123456\"\n}"
                            )
                    }, schema = @Schema(implementation = EditProfileRequest.class)
            )
            }
    )
    @PostMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> changeUserNameEmailAndPassword(@RequestBody Map<String, String> editProfileParametersMap,
                                                      Principal principal) {
        return profileService.editProfileWithoutPhoto(editProfileParametersMap, principal);

    }

    /**
     * Метод editProfile.
     * multipart/form-data POST запрос /api/profile/my
     * Изменение пароля и аватара пользователя.
     *
     * @param photo              - аватар пользователя.
     * @param editProfileRequest - тело запроса на изменение.
     * @param authentication          - авторизованный пользователь.
     * @return - Response
     */
    @Parameter(name = "avatar", description = "Аватар", content = {@Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(name = "photo", implementation = MultipartFile.class))})
    @PostMapping(value = "/my", consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> editProfile(@RequestParam(required = false) MultipartFile photo,
                                   @ModelAttribute EditProfileRequest editProfileRequest, Authentication authentication) {
        return profileService.editProfileWithPhoto(editProfileRequest, photo, authentication);
    }
}
