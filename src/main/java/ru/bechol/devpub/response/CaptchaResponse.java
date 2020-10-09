package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaptchaResponse {
    private String code;
    private String secret;
    private String image;
}
