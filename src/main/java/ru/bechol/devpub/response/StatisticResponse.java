package ru.bechol.devpub.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс StatisticResponse.
 * Ответ на GET запрос /api/statistics/my
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticResponse {

	long postsCount;
	long likesCount;
	long dislikesCount;
	int viewsCount;
	long firstPublication;

}
