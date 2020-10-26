package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;
import ru.bechol.devpub.models.User;

import java.util.List;

/**
 * Класс PostsResponse.
 * Сериализация информации о постах и их количестве в выборке.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@Getter
@Builder
public class PostsResponse {

    private int count;
    private List<PostsResponse.PostBody> posts;

    /**
     * Внутренний статический класс PostBody.
     * Сериализация информации о постах.
     */
    @Getter
    @Builder
    public static class PostBody {
        private long id;
        private long timestamp;
        private String title;
        private String announce;
        private User user;
        private long likeCount;
        private long dislikeCount;
        private long commentCount;
        private long viewCount;
    }
}
