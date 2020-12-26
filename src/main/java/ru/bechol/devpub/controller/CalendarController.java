package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.response.CalendarResponse;
import ru.bechol.devpub.service.IPostService;

/**
 * Класс CalendarController.
 * REST контроллер для запросов через /api/calendar
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Tag(name = "/api/calendar", description = "Календарь")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

	@Autowired
	@Qualifier("postService")
	IPostService postService;

	/**
	 * Метод calendarData.
	 * GET запрос /api/calendar.
	 * Вывод количество публикаций на каждую дату переданного в параметре year года или текущего года,
	 * если параметр year не задан. В параметре years всегда возвращается список всех годов,
	 * за которые была хотя бы одна публикация, в порядке возврастания.
	 *
	 * @param year - год, выбранный в календаре.
	 * @return CalendarResponse.
	 */
	@Operation(summary = "Информация для календаря", description = "Метод выводит количества публикаций на каждую дату " +
			"переданного в параметре year года или текущего года, если параметр year не задан. " +
			"В параметре years всегда возвращается список всех годов, за которые была хотя бы одна публикация, " +
			"в порядке возврастания.")
	@GetMapping
	public CalendarResponse calendarData(@RequestParam(required = false) Integer year) {
		return postService.createCalendarData(String.valueOf(year));
	}

}
