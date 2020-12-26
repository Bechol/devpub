package ru.bechol.devpub.response.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CloudinaryResult {

	String secureUrl;
	String publicId;
}
