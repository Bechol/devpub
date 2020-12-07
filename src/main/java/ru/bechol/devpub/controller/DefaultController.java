package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.response.GeneralInfoResponse;
import ru.bechol.devpub.service.GlobalSettingsService;

import java.security.Principal;
import java.util.*;

/**
 * Класс DefaultController.
 * REST контроллер.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see GlobalSettingsService
 */
@Tag(name = "/api", description = "Общая информация о блоге, глобальные настройки")
@RestController
@RequestMapping("/api")
public class DefaultController {

    @Autowired
    private GeneralInfoResponse generalInfoResponse;

    @Autowired
    private GlobalSettingsService globalSettingsService;

    /**
     * Метод getGeneralInfo.
     * GET запрос /api/init.
     * Возвращает общую информацию о блоге: название блога и подзаголовок для размещения в хэдере сайта,
     * а также номер телефона, e-mail и информацию об авторских правах для размещения в футере.
     *
     * @return общая информация о блоге в json формате.
     */
    @Operation(summary = "Общая информацию о блоге", description = "Возвращает название блога и " +
            "подзаголовок для размещения в хэдере сайта, а также номер телефона, e-mail и информацию об авторских " +
            "правах для размещения в футере.")
    @ApiResponse(responseCode = "200")
    @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<GeneralInfoResponse> getGeneralInfo() {
        return ResponseEntity.ok(generalInfoResponse);
    }

    /**
     * Метод getGeneralSettings.
     * GET запрос /api/settings
     * Возвращает глобальные настройки блога из таблицы global_settings.
     *
     * @return глобальные настройки блога из таблицы global_settings.
     */
    @Operation(summary = "Возвращает глобальные настройки блога")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Глобальные настройки найдены в базе.",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                            @ExampleObject(description = "Может быть одна или несколько настроек",
                                    value = "{\n\t\"MULTIUSER_MODE\": true," +
                                            "\n\t\"POST_PREMODERATION\": false," +
                                            "\n\t\"STATISTICS_IS_PUBLIC\": true\n}"),
                    }, schema = @Schema(implementation = Map.class))
                    }),
            @ApiResponse(responseCode = "204", description = "Настройки не найдены в базе",
                    content = {@Content(schema = @Schema(hidden = true))}),
    })
    @GetMapping(value = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Map<String, Boolean>> getGeneralSettings() {
        return globalSettingsService.createGeneralSettingsMap();
    }

    /**
     * Метод updateGeneralSettings.
     * PUT запрос /api/settings
     * Метод записывает глобальные настройки блога в таблицу global_settings,
     * если запрашивающий пользователь авторизован и является модератором.
     *
     * @return глобальные настройки блога из таблицы global_settings.
     */
    @Operation(summary = "Запись глобальных настроек блога, если запрашивающий пользователь авторизован " +
            "и является модератором. Tребует авторизации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Новые значения настроек успешно записаны",
                    content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "400", description = "Если авторизованный пользователь не является модератором",
                    content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "404", description = "Ни одна из указанных в теле запроса настроек не найдена",
                    content = {@Content(schema = @Schema(hidden = true))}),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Перечень настроек, которые необходимо изменить",
            required = true, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(description = "Может быть одна или несколько настроек",
                            value = "{\n\t\"MULTIUSER_MODE\": true," +
                                    "\n\t\"POST_PREMODERATION\": false," +
                                    "\n\t\"STATISTICS_IS_PUBLIC\": true\n}"),
            })
    })
    @PutMapping(value = "/settings",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> updateGeneralSettings(@RequestBody Map<String, Boolean> settings, Principal principal) {
        if (Objects.isNull(principal) || !Strings.isNotEmpty(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return globalSettingsService.updateGeneralSettings(settings, principal);
    }
}
