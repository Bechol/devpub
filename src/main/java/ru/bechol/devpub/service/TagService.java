package ru.bechol.devpub.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.Tag;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.repository.TagRepository;
import ru.bechol.devpub.response.TagResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс TagService.
 * Сервисный слой для Tag.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.Tag
 * @see TagRepository
 */
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;

    /**
     * Метод findAllTagsByQuery.
     * Формирование ответа для GET /api/tag.
     * @param query - строка запроса для выборки тегов.
     * @return ResponseEntity<TagResponse>.
     */
    public ResponseEntity<TagResponse> findAllTagsByQuery(String query) {
        List<Tag> tagsByQuery = Strings.isNotEmpty(query) ? tagRepository.findByQuery(query) : tagRepository.findAll();
        List<TagResponse.TagElement> tagsNodes = tagsByQuery.stream()
                .peek(tag -> tag.getPosts().removeIf(this::checkPost))
                .map(tag -> TagResponse.TagElement.builder()
                        .name(tag.getName())
                        .weight(tag.getPosts().size() > 0 ? tag.getPosts().size() / (float) postRepository.count() : 0)
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(TagResponse.builder().tags(tagsNodes).build());
    }

    /**
     * Метод checkPost.
     * Проверка поста по критериям: активный, утвержден, время публикации не превышает текущее.
     *
     * @param post - пост, который необходимо проверить.
     * @return true - если не соблюдены все условия проверки.
     */
    private boolean checkPost(Post post) {
        return !(post.isActive() && post.getModerationStatus().equals(Post.ModerationStatus.ACCEPTED) &&
                post.getTime().isBefore(LocalDateTime.now()));
    }
}
