package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

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
public class StatisticResponse {

    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private int viewsCount;
    private long firstPublication;

}
