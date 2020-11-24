package ru.bechol.devpub.service;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.response.Response;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс ProfileService.
 * Сервис для изменения профиля пользователя.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Service
public class ProfileService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Messages messages;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private StorageService storageService;

    /**
     * Метод editProfileWithoutPhoto.
     * Изменение email, имени пользователя или пароля без удаления или изменения фото.
     *
     * @param editParametersMap - новые параметры профиля.
     * @param principal         - авторизованный пользователь.
     * @return - ResponseEntity.
     */
    public Response<?> editProfileWithoutPhoto(Map<String, String> editParametersMap, Principal principal) {
        Map<String, String> errorsMap = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        handleEditParameters(editParametersMap, user, errorsMap);
        if (errorsMap.size() > 0) {
            return Response.builder().result(false).errors(errorsMap).build();
        }
        userRepository.save(user);
        return Response.builder().result(true).build();
    }

    /**
     *
     * @param editProfileRequest
     * @param file
     * @param principal
     * @return
     */
    public Response<?> editProfileWithPhoto(EditProfileRequest editProfileRequest, MultipartFile file,
                                            Principal principal) {
        Map<String, String> errorsMap = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> editProfileParametersMap = objectMapper.convertValue(editProfileRequest, Map.class);
        handleEditParameters(editProfileParametersMap, user, errorsMap);
        StorageService.CloudinaryResult cloudinaryResult = null;
        try {
            cloudinaryResult = storageService.sendToCloudinary(file);
            user.setPhotoLink(cloudinaryResult.getSecureUrl());
            user.setPhotoPublicId(cloudinaryResult.getPublicId());
        } catch (IOException exception) {
            return Response.builder().result(false).build();
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
    }

    /**
     * Метод validateEmail.
     * Проверяет и изменяет email пользователя.
     *
     * @param user      - авторизованный пользователь.
     * @param newEmail  - новый адрес email.
     * @param errorsMap - мапа с ошибками.
     */
    private void validateEmail(User user, String newEmail, Map<String, String> errorsMap) {
        if (!Strings.isNotEmpty(newEmail)) {
            errorsMap.put("name", messages.getMessage("ve.email", newEmail));
            return;
        }
        if (!newEmail.equals(user.getEmail()) && userRepository.findByEmail(newEmail).isPresent()) {
            errorsMap.put("email", messages.getMessage("ve.email-exists", user.getEmail()));
        } else {
            user.setEmail(newEmail);
        }
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
        if (!Strings.isNotEmpty(newUserName)) {
            errorsMap.put("name", messages.getMessage("ve.user-name", newUserName));
            return;
        }
        if (!newUserName.equals(user.getName()) && userRepository.findByName(newUserName).isPresent()) {
            errorsMap.put("name", messages.getMessage("ve.name-exists", newUserName));
        } else {
            user.setName(newUserName);
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
        if (!Strings.isNotEmpty(newPassword)) {
            errorsMap.put("password", messages.getMessage("ve.password-null", newPassword));
            return;
        }
        if (Strings.isNotEmpty(newPassword) && newPassword.length() < 6) {
            errorsMap.put("password", messages.getMessage("ve.password-length", 6));
            return;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
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
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


}
