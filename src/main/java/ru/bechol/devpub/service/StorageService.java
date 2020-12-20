package ru.bechol.devpub.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.response.Response;

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
@Service
public class StorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Messages messages;

    @Value("${storage.root.location}")
    private String uploadPath;

    @Value("${storage.image.allowed-formats}")
    private List<String> allowedImageFormats;

    /**
     * Метод uploadFile.
     * Загрузка изображений на Cloudinary.
     *
     * @param file - multipart файл изображения.
     * @return ResponseEntity<?>.
     */
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
            FileSystemUtils.deleteRecursively(new File(uploadPath + "/" + file.getName()));
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
     * @param file - файл загруженный с пользоваельской формы.
     * @return - CloudinaryResult
     * @throws IOException
     */
    public CloudinaryResult sendToCloudinary(MultipartFile file) throws IOException {
        File uploadedFile = convertMultiPartToFile(file);
        Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
        return CloudinaryResult.builder()
                .secureUrl((String) uploadResult.get("secure_url"))
                .publicId((String) uploadResult.get("public_id"))
                .build();
    }

    /**
     * Метод convertMultiPartToFile.
     * Конвертация multipart файла в файл.
     *
     * @param file - multipart файл.
     * @return - File.
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
     * @param file - загружаемый файл картинки.
     * @return - true, если формат загружаемой картинки найден среди указанных в настройках приложения.
     */
    private boolean checkImageFormat(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return allowedImageFormats.contains(extension);
    }

    /**
     * Класс CloudinaryResult.
     * Десериализация ответа от cloudinary.
     *
     * @author Oleg Bech
     * @version 1.0
     * @email oleg071984@gmail.com
     */
    @Getter
    @Builder
    public static class CloudinaryResult {
        private String secureUrl;
        private String publicId;
    }
}
