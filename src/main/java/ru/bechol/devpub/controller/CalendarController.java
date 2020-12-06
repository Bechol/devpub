package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.CalendarResponse;
import ru.bechol.devpub.service.PostService;

/**
 * Класс CalendarController.
 * REST контроллер для запросов через /api/calendar
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Tag(name = "/api/calendar", description = "Календарь")
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private PostService postService;

    /**
     * Метод calendarData.
     * GET запрос /api/calendar.
     * Вывод количество публикаций на каждую дату переданного в параметре year года или текущего года,
     * если параметр year не задан. В параметре years всегда возвращается список всех годов,
     * за которые была хотя бы одна публикация, в порядке возврастания.
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
