package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.response.dto.CloudinaryResult;

import java.io.IOException;

/**
 * Интерфейс IStorageService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Service
public interface IStorageService {

	/**
	 * Метод uploadFile.
	 * Загрузка файла.
	 *
	 * @param file multipart файл.
	 * @return ResponseEntity<?>.
	 */
	ResponseEntity<?> uploadFile(MultipartFile file);

	/**
	 * Метод sendToCloudinary.
	 * Отправка/загрузка файла картинки на cloudinary.
	 *
	 * @param file файл загруженный с пользоваельской формы.
	 * @return CloudinaryResult
	 * @throws IOException
	 */
	CloudinaryResult sendToCloudinary(MultipartFile file) throws IOException;
}
