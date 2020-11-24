package ru.bechol.devpub.service.enums;

import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.exception.ModerationStatusNotFoundException;
import ru.bechol.devpub.service.exception.PostStatusNotFoundException;

import java.util.stream.Stream;

/**
 * Класс SortMode.
 * Возможные статусы модерации.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.PostController
 * @see PostService
 */
public enum ModerationStatus {

    NEW("new"),
    ACCEPTED("accepted"),
    DECLINED("declined");

    public final String value;

    ModerationStatus(String statusValue) {
        this.value = statusValue;
    }

    /**
     * Метод fromValue.
     * Поиск значения enum по значению.
     *
     * @param requestModerationStatusValue - значение для поиска.
     * @return - значение enum.
     * @throws PostStatusNotFoundException - если искомое значение enum не существует.
     */
    public static ModerationStatus fromValue(String requestModerationStatusValue)
            throws ModerationStatusNotFoundException {
        return Stream.of(values())
                .filter(postStatus -> postStatus.value.equals(requestModerationStatusValue)).findAny()
                .orElseThrow(() -> new ModerationStatusNotFoundException(
                        String.format("Moderation status [%s] is not present", requestModerationStatusValue)));
    }
}
