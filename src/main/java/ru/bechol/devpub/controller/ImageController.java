package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.StorageService;

/**
 * Класс ImageController.
 * REST контроллер для запросов на "/api/image"
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Tag(name = "/api/image", description = "Загрузка изображений")
@ApiResponses(value = {
        @ApiResponse(description = "Изображение загружено", responseCode = "200", content = {
                @Content(examples = {@ExampleObject(value = "http://res.cloudinary.com/demo/raw/upload/v****/image.png")})
        }),
        @ApiResponse(responseCode = "400", description = "Ошибки при выполненении запроса",
                content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                        @ExampleObject(value = "{\n\"image\": \"Размер файла превышает допустимый размер\"\n}")},
                        schema = @Schema(implementation = Response.class))
                }),
        @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                content = {@Content(schema = @Schema(hidden = true))})
})
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private StorageService storageService;

    /**
     * Метод uploadImage
     * POST запрос /api/image.
     * Метод загружает на cloudinary изображение и возвращает ссылку.
     *
     * @param image - файл картинки для загрузки
     * @return ResponseEntity.
     */
    @Operation(summary = "Загрузка изображения на сервис Cloudinary", description = "Метод возвращает путь до " +
            "изображения. Этот путь затем будет вставлен в HTML-код публикации, в которую вставлено изображение.")
    @RequestBody(content = {@Content(schema = @Schema(hidden = true))})
    @Parameter(name = "image", description = "Файл изображения в формате jpg или png",
            content = {@Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(name = "image", implementation = MultipartFile.class))})
    @PostMapping
    public ResponseEntity uploadImage(@RequestPart(required = false) MultipartFile image) {
        return storageService.uploadFile(image);
    }
}
