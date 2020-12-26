package ru.bechol.devpub.service.enums;

import ru.bechol.devpub.service.IPostService;
import ru.bechol.devpub.service.exception.EnumValueNotFoundException;

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
	 * @throws EnumValueNotFoundException - если искомое значение enum не существует.
	 */
	public static ModerationStatus fromValue(String requestModerationStatusValue)
			throws EnumValueNotFoundException {
		return Stream.of(values())
				.filter(postStatus -> postStatus.value.equals(requestModerationStatusValue)).findAny()
				.orElseThrow(() -> new EnumValueNotFoundException(
						String.format("Enum value [%s] is not present", requestModerationStatusValue)));
	}
}
