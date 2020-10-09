package ru.bechol.devpub.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class GeneralInfoResponse {

    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;

}
