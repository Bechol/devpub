package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserData {

    private long id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
