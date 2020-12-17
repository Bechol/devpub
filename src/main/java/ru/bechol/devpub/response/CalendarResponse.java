package ru.bechol.devpub.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalendarResponse {

	List<String> years;
	Map<String, Long> posts;
}
