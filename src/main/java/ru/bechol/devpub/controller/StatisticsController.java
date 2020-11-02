package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.StatisticResponse;
import ru.bechol.devpub.service.UserService;

import java.security.Principal;

/**
 * Класс StatisticsController.
 * REST контроллер для обычных запросов не через /api/statistics.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private UserService userService;

    /**
     * Метод calculateMyStatistics.
     * GET запрос /api/statistics/my
     * Вывод статистики по активным постам авторизованного пользователя.
     *
     * @param principal - авторизованный пользователь.
     * @return StatisticResponse.
     */
    @GetMapping("/my")
    public StatisticResponse calculateMyStatistics(Principal principal) {
        return userService.calculateMyStatistics(principal);
    }
}
