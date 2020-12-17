package ru.bechol.devpub.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Класс GeneralInfoResponse.
 * Сериализация основной информации о блоге.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.configuration.GeneralInfoConfig
 * @see ru.bechol.devpub.controller.DefaultController
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeneralInfoResponse {

	String title;
	String subtitle;
	String phone;
	String email;
	String copyright;
	String copyrightFrom;

}
