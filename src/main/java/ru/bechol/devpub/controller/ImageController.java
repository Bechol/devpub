package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.service.StorageService;

/**
 * Класс ImageController.
 * REST контроллер для запросов на "/api/image"
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private StorageService storageService;

    /**
     * Метод uploadImage
     * POST запрос /api/image.
     * Метод загружает на cloudinary изображение и возвращает ссылку.
     * @param image - файл картинки для загрузки
     * @return ResponseEntity.
     */
    @PostMapping
    public ResponseEntity uploadImage(@RequestPart(required = false) MultipartFile image) {
        return storageService.uploadFile(image);
    }
}
