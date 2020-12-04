package ru.bechol.devpub.service.enums;

import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.exception.SortModeNotFoundException;

import java.util.stream.Stream;

/**
 * Класс SortMode.
 * Enum режимов сортировки постов.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.PostController
 * @see PostService
 */
public enum SettingValue {

    YES("yes"),
    NO("no");

    public final String value;

    SettingValue(String modeValue) {
        this.value = modeValue;
    }

    /**
     * Метод fromValue.
     * Поиск значения enum по значению.
     *
     * @param requestModeValue - значение для поиска.
     * @return - значение enum.
     * @throws SortModeNotFoundException - если искомое значение enum не существует.
     */
    public static SettingValue fromValue(String requestModeValue) throws SortModeNotFoundException {
        return Stream.of(values())
                .filter(sortMode -> sortMode.value.equals(requestModeValue)).findAny()
                .orElseThrow(() -> new SortModeNotFoundException(
                        String.format("Sort mode [%s] is not present", requestModeValue)));
    }
}
