package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Класс PostResponse.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see PostDto
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {

    private int count;
    private List<PostDto> posts;
    private PostDto post;
}
