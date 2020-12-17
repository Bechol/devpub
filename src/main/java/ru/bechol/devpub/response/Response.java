package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Response<T> {

	Boolean result;
	T user;
	@JsonUnwrapped
	T post;
	T errors;

}
