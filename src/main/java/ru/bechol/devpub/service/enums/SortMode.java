package ru.bechol.devpub.service.enums;

import ru.bechol.devpub.service.IPostService;
import ru.bechol.devpub.service.exception.EnumValueNotFoundException;

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
public enum SortMode {

	BEST("best"),
	EARLY("early"),
	POPULAR("popular"),
	RECENT("recent");

	public final String value;

	SortMode(String modeValue) {
		this.value = modeValue;
	}

	/**
	 * Метод fromValue.
	 * Поиск значения enum по значению.
	 *
	 * @param requestModeValue - значение для поиска.
	 * @return - значение enum.
	 * @throws EnumValueNotFoundException - если искомое значение enum не существует.
	 */
	public static SortMode fromValue(String requestModeValue) throws EnumValueNotFoundException {
		return Stream.of(values())
				.filter(sortMode -> sortMode.value.equals(requestModeValue)).findAny()
				.orElseThrow(() -> new EnumValueNotFoundException(
						String.format("Enum value [%s] is not present", requestModeValue)));
	}
}
