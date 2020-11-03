package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Класс CalendarResponse.
 * Ответ на GET запрос /api/calendar.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.CalendarController
 * @see ru.bechol.devpub.service.PostService
 */
@Getter
@Builder
public class CalendarResponse {
    List<String> years;
    Map<String, Long> posts;
}
