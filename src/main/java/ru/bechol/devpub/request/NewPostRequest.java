package ru.bechol.devpub.request;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class NewPostRequest {

    private long timestamp;
    private boolean active;
    @Size(min = 3, max = 100, message = "Длина заголовка должна быть не менее 3-х и не более 100-а символов.")
    private String title;
    private String[] tags;
    @Size(min = 50, max = 2000, message = "Длина текста должна быть не менее 50-ти и не более 2000 символов.")
    private String text;
}
