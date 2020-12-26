package ru.bechol.devpub.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.repository.IUserRepository;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.response.dto.CloudinaryResult;
import ru.bechol.devpub.service.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Класс StorageService.
 * Сервис для работы с Cloudinary и формирования ответа для StorageController.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class StorageService implements IStorageService {

	@Autowired
	Cloudinary cloudinary;
	@Autowired
	@Qualifier("userService")
	IUserService userService;
	@Autowired
	IUserRepository userRepository;
	@Autowired
	Messages messages;
	@Value("${storage.root.location}")
	String uploadPath;
	@Value("${storage.image.allowed-formats}")
	List<String> allowedImageFormats;

	/**
	 * Метод uploadFile.
	 * Загрузка изображений.
	 *
	 * @param file multipart файл изображения.
	 * @return ResponseEntity<?>.
	 */
	@Override
	public ResponseEntity<?> uploadFile(MultipartFile file) {
		Map<String, String> errorMap = new HashMap<>();
		try {
			if (!this.checkImageFormat(file)) {
				return ResponseEntity.badRequest().body(Response.builder().result(false)
						.errors(Map.of("image-format",
								messages.getMessage("er.image-format", allowedImageFormats)
								)
						)
						.build()
				);
			}
			return ResponseEntity.ok(sendToCloudinary(file).getSecureUrl());
		} catch (IOException | NullPointerException exception) {
			log.error(exception.getMessage());
			errorMap.put("file", exception.getMessage());
			return ResponseEntity.badRequest().body(Response.builder().result(false).errors(errorMap).build());
		}
	}

	/**
	 * Метод sendToCloudinary.
	 * Отправка/загрузка файла картинки на cloudinary.
	 *
	 * @param file файл загруженный с пользоваельской формы.
	 * @return CloudinaryResult
	 * @throws IOException
	 */
	@Override
	public CloudinaryResult sendToCloudinary(MultipartFile file) throws IOException {
		File uploadedFile = convertMultiPartToFile(file);
		Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
		Path deletableFile = Paths.get(uploadPath + "/" + file.getOriginalFilename());
		Files.delete(deletableFile);
		return CloudinaryResult.builder()
				.secureUrl((String) uploadResult.get("secure_url"))
				.publicId((String) uploadResult.get("public_id"))
				.build();
	}

	/**
	 * Метод convertMultiPartToFile.
	 * Конвертация multipart файла в файл.
	 *
	 * @param file multipart файл.
	 * @return File.
	 * @throws IOException
	 */
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		Path rootLocation = Paths.get(uploadPath);
		File uploadDir = new File(rootLocation.toUri());
		if (!uploadDir.exists()) {
			log.warn("create temp upload file directory");
			uploadDir.mkdirs();
		}
		File convFile = rootLocation.resolve(StringUtils.cleanPath(file.getOriginalFilename())).toFile();
		FileOutputStream fileOutputStream = new FileOutputStream(convFile);
		fileOutputStream.write(file.getBytes());
		fileOutputStream.close();
		return convFile;
	}

	/**
	 * Метод checkImageFormat.
	 * Проверка формата изображения.
	 *
	 * @param file загружаемый файл картинки.
	 * @return true, если формат загружаемой картинки найден среди указанных в настройках приложения.
	 */
	private boolean checkImageFormat(MultipartFile file) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		return allowedImageFormats.contains(extension);
	}
}
