package ru.bechol.devpub.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс EditProfileRequest.
 * Для десеарилизации тела запроса на изменение email, пароля и имени пользователя с удалением фото.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.controller.ApiAuthController
 * @see ru.bechol.devpub.controller.CommentController
 * @see ru.bechol.devpub.controller.ProfileController
 * @see ru.bechol.devpub.service.ProfileService
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditProfileRequest {

	String email;
	String password;
	String name;
	String removePhoto;

}
