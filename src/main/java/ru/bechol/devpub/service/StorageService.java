package ru.bechol.devpub.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.response.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${storage.max-size}")
    private long maximumFileSize;

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
        if (!this.checkImageFormat(file)) {
            errorMap.put("image-format", messages.getMessage("er.image-format", allowedImageFormats));
        }
        if (file.getSize() > maximumFileSize) {
            errorMap.put("image-size", messages.getMessage("er.image-size", maximumFileSize / 1000, "KB"));
        }
        if (!errorMap.isEmpty()) {
            return ResponseEntity.ok(Response.builder().result(false).errors(errorMap).build());
        }
        try {
            File uploadedFile = convertMultiPartToFile(file);
            Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            String result = (String) uploadResult.get("secure_url");
            FileSystemUtils.deleteRecursively(Paths.get(uploadPath));
            return ResponseEntity.ok(result);
        } catch (IOException | NullPointerException exception) {
            log.error(exception.getMessage());
            errorMap.put("file", exception.getMessage());
            return ResponseEntity.ok(Response.builder().result(false).errors(errorMap).build());
        }
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
}