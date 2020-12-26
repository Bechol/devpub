package ru.bechol.devpub.service.enums;

import ru.bechol.devpub.service.IPostService;
import ru.bechol.devpub.service.exception.EnumValueNotFoundException;

import java.util.stream.Stream;

/**
 * Класс SortMode.
 * Возможные статусы постов.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.PostController
 * @see PostService
 */
public enum PostStatus {

	NEW("new"),
	ACCEPTED("accepted"),
	INACTIVE("inactive"),
	PENDING("pending"),
	DECLINED("declined"),
	PUBLISHED("published");

	public final String value;

	PostStatus(String statusValue) {
		this.value = statusValue;
	}

	/**
	 * Метод fromValue.
	 * Поиск значения enum по значению.
	 *
	 * @param requestPostStatusValue - значение для поиска.
	 * @return - значение enum.
	 * @throws EnumValueNotFoundException - если искомое значение enum не существует.
	 */
	public static PostStatus fromValue(String requestPostStatusValue) throws EnumValueNotFoundException {
		return Stream.of(values())
				.filter(postStatus -> postStatus.value.equals(requestPostStatusValue)).findAny()
				.orElseThrow(() -> new EnumValueNotFoundException(
						String.format("Enum value [%s] is not present", requestPostStatusValue)));
	}
}
