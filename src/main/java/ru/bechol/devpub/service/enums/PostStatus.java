package ru.bechol.devpub.service.enums;

import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.exception.PostStatusNotFoundException;

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
     * @throws PostStatusNotFoundException - если искомое значение enum не существует.
     */
    public static PostStatus fromValue(String requestPostStatusValue) throws PostStatusNotFoundException {
        return Stream.of(values())
                .filter(postStatus -> postStatus.value.equals(requestPostStatusValue)).findAny()
                .orElseThrow(() -> new PostStatusNotFoundException(
                        String.format("Post status [%s] is not present", requestPostStatusValue)));
    }
}
