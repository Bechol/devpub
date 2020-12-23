package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@JsonRootName("tags")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagResponse {

	List<TagElement> tags;

	@Data
	@Builder
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class TagElement {
		String name;
		float weight;
	}
}
