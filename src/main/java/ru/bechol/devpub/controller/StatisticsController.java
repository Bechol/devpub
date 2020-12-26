package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.response.StatisticResponse;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

/**
 * Класс StatisticsController.
 * REST контроллер.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@Tag(name = "/api/statistics", description = "Статистика")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

	@Autowired
	@Qualifier("statisticService")
	IStatisticService statisticService;

	/**
	 * Метод calculateMyStatistics.
	 * GET запрос /api/statistics/my
	 * Метод возвращает статистику постов текущего авторизованного пользователя: общие количества параметров
	 * для всех публикаций, у который он является автором и доступные для чтения.
	 *
	 * @param user авторизованный пользователь.
	 * @return StatisticResponse.
	 */
	@Operation(summary = "Статистика постов текущего авторизованного пользователя", description = "Метод возвращает " +
			"статистику постов текущего авторизованного пользователя: общие количества параметров для всех публикаций, " +
			"у который он является автором и доступных для чтения.")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR')")
	@GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
	public StatisticResponse calculateMyStatistics(@AuthenticationPrincipal User user) {
		return statisticService.calculateMyStatistics(user);
	}

	/**
	 * Метод calculateSiteStatistics.
	 * GET запрос /api/statistics/all
	 * Вывод статистики по всем постам блога.
	 *
	 * @return ResponseEntity.
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
	public ResponseEntity<?> calculateSiteStatistics(@AuthenticationPrincipal User user) throws CodeNotFoundException {
		return statisticService.calculateSiteStatistics(user);
	}
}
