package ru.bechol.devpub.controller;

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
    @GetMapping
    public CalendarResponse calendarData(@RequestParam(required = false) Integer year) {
        return postService.createCalendarData(String.valueOf(year));
    }

}
