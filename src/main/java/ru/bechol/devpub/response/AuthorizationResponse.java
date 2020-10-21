package ru.bechol.devpub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationResponse {

    private boolean result;
    @JsonProperty("user")
    private UserData userData;

    @Getter
    @Builder
    public static class UserData {
        private long id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean settings;
    }
}
