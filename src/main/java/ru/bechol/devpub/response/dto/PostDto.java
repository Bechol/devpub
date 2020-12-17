package ru.bechol.devpub.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Класс PostDto.
 * Cериализация поста.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.PostService
 * @see ru.bechol.devpub.service.helper.PostMapperHelper
 * @see CommentDto
 * @see UserDto
 * @see ru.bechol.devpub.models.Tag
 * @see ru.bechol.devpub.models.Post
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

	long id;
	long timestamp;
	Boolean active;
	UserDto user;
	String title;
	String announce;
	String text;
	long likeCount;
	long dislikeCount;
	long commentCount;
	long viewCount;
	List<CommentDto> comments;
	List<String> tags;
}
