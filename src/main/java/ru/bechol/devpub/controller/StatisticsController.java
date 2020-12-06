package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.StatisticResponse;
import ru.bechol.devpub.service.UserService;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

import java.security.Principal;

/**
 * Класс StatisticsController.
 * REST контроллер для обычных запросов не через /api/statistics.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@Tag(name = "/api/statistics", description = "Статистика")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private UserService userService;

    /**
     * Метод calculateMyStatistics.
     * GET запрос /api/statistics/my
     * Метод возвращает статистику постов текущего авторизованного пользователя: общие количества параметров
     * для всех публикаций, у который он является автором и доступные для чтения.
     *
     * @param principal - авторизованный пользователь.
     * @return StatisticResponse.
     */
    @Operation(summary = "Статистика постов текущего авторизованного пользователя", description = "Метод возвращает " +
            "статистику постов текущего авторизованного пользователя: общие количества параметров для всех публикаций, " +
            "у который он является автором и доступных для чтения.")
    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatisticResponse calculateMyStatistics(Principal principal) {
        return userService.calculateMyStatistics(principal);
    }

    /**
     * Метод calculateSiteStatistics.
     * GET запрос /api/statistics/all
     * Вывод статистики по всем постам блога.
     *
     * @return - ResponseEntity.
     */
    @Operation(summary = "Статистика по всем постам блога", description = "Метод возвращает " +
            "статистику постов текущего авторизованного пользователя: общие количества параметров для всех публикаций, " +
            "у который он является автором и доступных для чтения.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Новые значения настроек успешно записаны",
                    content = {@Content(schema = @Schema(implementation = StatisticResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Если авторизованный пользователь не является модератором " +
                    "или выключен публичный показ статистики",
                    content = {@Content(schema = @Schema(hidden = true))})
    })
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculateSiteStatistics(Principal principal) throws CodeNotFoundException {
        return userService.calculateSiteStatistics(principal);
    }
}
