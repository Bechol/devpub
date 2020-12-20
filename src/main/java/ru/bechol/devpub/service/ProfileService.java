package ru.bechol.devpub.service;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.response.Response;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;

/**
 * Класс ProfileService.
 * Сервис для изменения профиля пользователя.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileService {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    Messages messages;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    StorageService storageService;

    /**
     * Метод editProfile.
     * Изменение email, имени пользователя или пароля без удаления или изменения фото.
     *
     * @param editParametersMap - новые параметры профиля.
     * @return - ResponseEntity.
     */
    public ResponseEntity<?> editProfile(Map<String, String> editParametersMap, Authentication authentication)
            throws UserPrincipalNotFoundException {
        Map<String, String> errorsMap = new HashMap<>();
        User user = userService.findActiveUser(authentication);
        handleEditParameters(editParametersMap, user, errorsMap);
        if (errorsMap.size() > 0) {
            return ResponseEntity.ok(Response.builder().result(false).errors(errorsMap).build());
        }
        userRepository.save(user);
        return ResponseEntity.ok(Response.builder().result(true).build());
    }

    public Response<?> editProfile(MultipartFile file, EditProfileRequest editProfileRequest,
                                   Authentication authentication) throws IOException {
        User user = userService.findActiveUser(authentication);
        Map<String, String> errorsMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> editProfileParametersMap = objectMapper.convertValue(editProfileRequest, Map.class);
        handleEditParameters(editProfileParametersMap, user, errorsMap);
        StorageService.CloudinaryResult cloudinaryResult = storageService.sendToCloudinary(file);
        this.deletePhoto(user, "1");
        user.setPhotoLink(cloudinaryResult.getSecureUrl());
        user.setPhotoPublicId(cloudinaryResult.getPublicId());
        if (errorsMap.size() > 0) {
            return Response.builder().result(false).errors(errorsMap).build();
        }
        userRepository.save(user);
        return Response.builder().result(true).build();
    }

    /**
     * Метод handleEditParameters.
     * Обработка тела запроса на изменение данных профиля пользователя.
     *
     * @param editParametersMap - мапа параметров пользователя.
     * @param user              - авторизованный пользователь.
     * @param errorsMap         - мапа с ошибками.
     */
    private void handleEditParameters(Map<String, String> editParametersMap, User user, Map<String, String> errorsMap) {
        editParametersMap.keySet().forEach(userPropertyKey -> {
            log.info("start handling new profile parameters..");
            switch (userPropertyKey) {
                case "email":
                    this.validateEmail(user, editParametersMap.get("email"), errorsMap);
                    break;
                case "name":
                    this.validateName(user, editParametersMap.get("name"), errorsMap);
                    break;
                case "password":
                    this.validatePassword(user, editParametersMap.get("password"), errorsMap);
                    break;
                case "removePhoto":
                    this.deletePhoto(user, editParametersMap.get("removePhoto"));
                    break;
            }
        });
        log.info("Validation of new user profile finished");
    }

    /**
     * Метод validateEmail.
     * Проверяет и изменяет email пользователя.
     *
     * @param user      - авторизованный пользователь.
     * @param newEmail  - новый адрес email.
     * @param errorsMap - мапа с ошибками.
     */
    private User validateEmail(User user, String newEmail, Map<String, String> errorsMap) {
        if (Strings.isEmpty(newEmail)) {
            log.warn("new user email is null or empty");
            errorsMap.put("email", messages.getMessage("warning.email.is-empty"));
        } else if (newEmail.equals(user.getEmail())) {
            log.warn("changed email equals old email");
        } else if (!newEmail.equals(user.getEmail()) && userRepository.findByEmail(newEmail).isPresent()) {
            log.warn("new user email already exists in database");
            errorsMap.put("email", messages.getMessage("warning.user.already-exist.by-email"));
        } else {
            user.setEmail(newEmail);
            log.info("new user email has been successfully changed");
        }
        return user;
    }

    /**
     * Метод validateName.
     * Проверяет и изменяет имя пользователя.
     *
     * @param user        - авторизованный пользователь.
     * @param newUserName - новое имя пользователя.
     * @param errorsMap   - мапа с ошибками.
     */
    private void validateName(User user, String newUserName, Map<String, String> errorsMap) {
        if (Strings.isEmpty(newUserName)) {
            log.warn("new user name is null or empty");
            errorsMap.put("name", messages.getMessage("warning.username.is-empty"));
        } else if (newUserName.equals(user.getName())) {
            log.warn("new user name equals old name");
        } else if (!newUserName.equals(user.getName()) && userRepository.findByName(newUserName).isPresent()) {
            log.warn("new user name already exists in database");
            errorsMap.put("name", messages.getMessage("warning.user.already-exist.by-name"));
        } else {
            user.setName(newUserName);
            log.info("new user name has been successfully changed");
        }
    }

    /**
     * Метод validatePassword.
     * Проверяет и изменяет пароль пользователя.
     *
     * @param user        - авторизованный пользователь.
     * @param newPassword - новый пароль пользователя.
     * @param errorsMap   - мапа с ошибками.
     */
    private void validatePassword(User user, String newPassword, Map<String, String> errorsMap) {
        if (Strings.isEmpty(newPassword)) {
            errorsMap.put("password", messages.getMessage("warning.password.is-empty"));
            log.warn("new user password is null or empty");
        } else if (Strings.isNotEmpty(newPassword) && newPassword.length() < 6) {
            log.warn("new user password is not correct: length less than 6 symbols");
            errorsMap.put("password", messages.getMessage("ve.password-length", 6));
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            log.info("old user password has been successfully changed");
        }
    }

    /**
     * Метод deletePhoto.
     * Удаляет аватар пользователя.
     *
     * @param user        - авторизованный пользователь
     * @param removePhoto - если равен 1, то удаляем фото (запись из базы и саму аватарку из Cloudinary)
     */
    private void deletePhoto(User user, String removePhoto) {
        try {
            if (removePhoto.equals("1") && Strings.isNotEmpty(user.getPhotoPublicId())) {
                cloudinary.uploader().destroy(user.getPhotoPublicId(), null);
                user.setPhotoLink(null);
                user.setPhotoPublicId(null);
                log.info("user avatar has been successfully deleted");
            }
        } catch (IOException exception) {
            log.warn("user avatar was not deleted, got exception in cloudinary support");
            exception.printStackTrace();
        }
    }


}
