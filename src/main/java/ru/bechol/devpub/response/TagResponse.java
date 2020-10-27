package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonRootName("tags")
public class TagResponse {

    List<TagElement> tags;

    @Getter
    @Builder
    public static class TagElement {
        private String name;
        private float weight;
    }
}
