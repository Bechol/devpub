package ru.bechol.devpub.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Класс EditProfileRequest.
 * Запрос на изменение фото и пароля пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditProfileRequest {

    private String email;
    private String password;
    private String name;
    private String removePhoto;

}
