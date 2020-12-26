package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.response.Response;

import java.io.IOException;
import java.util.Map;

/**
 * Интерфейс IProfileService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IProfileService {

	/**
	 * Метод editProfile.
	 * Изменение email, имени пользователя или пароля без удаления или изменения фото.
	 *
	 * @param editParametersMap новые параметры профиля.
	 * @return ResponseEntity.
	 */
	ResponseEntity<?> editProfile(Map<String, String> editParametersMap, User user);

	/**
	 * Метод editProfile.
	 *
	 * @param file               загружаемый файл.
	 * @param editProfileRequest измененные данные пользователя.
	 * @param user               авторизованный пользователь
	 * @return Response<?>
	 * @throws IOException
	 */
	Response<?> editProfile(MultipartFile file, EditProfileRequest editProfileRequest, User user)
			throws IOException;


}
