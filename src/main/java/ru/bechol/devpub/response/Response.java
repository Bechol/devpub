package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private final Boolean result;
    private final T user;
    @JsonUnwrapped
    private final T post;
    private final T errors;

}
