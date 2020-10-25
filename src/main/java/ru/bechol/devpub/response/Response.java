package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private final boolean result;
    private final T user;
    private final T errors;

}
