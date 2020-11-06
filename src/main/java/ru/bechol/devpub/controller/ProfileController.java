package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bechol.devpub.request.EditProfileRequest;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.ProfileService;

import java.security.Principal;
import java.util.Map;

/**
 * Класс ProfileController.
 * REST контроллер для запросов /api/profile.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Метод changeUserNameEmailAndPassword.
     * POST запрос /api/profile/my
     * Изменение email, имени или пароля пользователя. Удаление аватара.
     *
     * @param editProfileParametersMap - мапа с параметрами из тела запроса.
     * @param principal                - авотризованный пользователь.
     * @return - Response.
     */
    @PostMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> changeUserNameEmailAndPassword(@RequestBody Map<String, String> editProfileParametersMap,
                                                      Principal principal) {
        return profileService.editProfileWithoutPhoto(editProfileParametersMap, principal);

    }

    /**
     * Метод editProfile.
     * multipart/form-data POST запрос /api/profile/my
     * Изменение пароля и аватара пользователя.
     *
     * @param photo              - аватар пользователя.
     * @param editProfileRequest - тело запроса на изменение.
     * @param principal          - авторизованный пользователь.
     * @return - Response
     */
    @PostMapping(value = "/my", consumes = "multipart/form-data")
    public Response<?> editProfile(@RequestParam(required = false) MultipartFile photo,
                                   @ModelAttribute EditProfileRequest editProfileRequest, Principal principal) {
        return profileService.editProfileWithPhoto(editProfileRequest, photo, principal);
    }
}
