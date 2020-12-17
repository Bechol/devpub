package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bechol.devpub.response.dto.PostDto;

import java.util.List;

/**
 * Класс PostResponse.
 * Сериализация полной информации о посте.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see PostDto
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {

	long count;
	List<PostDto> posts;
	PostDto post;
}
