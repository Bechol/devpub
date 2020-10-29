package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.bechol.devpub.models.Role;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.RoleRepository;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.request.ChangePasswordRequest;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.response.UserData;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс UserService.
 * Реализация сервисного слоя для User.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.User
 * @see UserRepository
 */
@Service
public class UserService implements UserDetailsService {

    private final static String ROLE_USER = "ROLE_USER";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Messages messages;
    @Autowired
    private Map<String, Long> sessionMap;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CaptchaCodesService captchaCodesService;

    /**
     * Метод registrateNewUser.
     * Регистрация нового пользователя.
     *
     * @param registerRequest данные с формы, прошедшие валидацию.
     * @return ResponseEntity<?>.
     */
    public ResponseEntity<?> registrateNewUser(RegisterRequest registerRequest, BindingResult bindingResult) {
        if (!captchaCodesService.captchaIsExist(registerRequest.getCaptcha(), registerRequest.getCaptcha_secret())) {
            bindingResult.addError(new FieldError(
                    "captcha", "captcha", messages.getMessage("cp.errors.captcha-code")));
        }
        if (bindingResult.hasErrors()) {
            return createResponseWithErrorMap(bindingResult);
        }
        User user = new User();
        user.setEmail(registerRequest.getE_mail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setModerator(false);
        userRepository.save(setUserRole(user));
        return ResponseEntity.ok().body(Response.builder().result(true).build());
    }

    /**
     * Метод setUserRole.
     * Присвоение роли ROLE_USER вновь зарегистрированному пользователь.
     *
     * @param user - пользователь.
     * @return пользователь с роллью user.
     */
    private User setUserRole(User user) {
        Set<Role> userRoles = user.getRoles();
        roleRepository.findByName(ROLE_USER).ifPresent(userRoles::add);
        user.setRoles(userRoles);
        return user;
    }

    /**
     * Метод findByEmail.
     * Поиск пользователя по email.
     *
     * @param email -  email пользователя для поиска.
     * @return Optional<User>.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Метод loadUserByUsername
     * Поиск пользователя по email в базе.
     *
     * @param email - email введеный на форме авторизации и аутентификации.
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messages.getMessage("user.not-found.by-email", email)
                ));
    }

    /**
     * Метод checkAuthorization.
     * Проверка авторизации по сессии.
     *
     * @param authorizedUser авторизованный пользователь.
     * @return ResponseEntity.
     */
    public ResponseEntity<?> checkAuthorization(User authorizedUser) {
        ResponseEntity<?> falseResponse = ResponseEntity.ok().body(Response.builder().result(false).build());
        if (authorizedUser == null) {
            return falseResponse;
        }
        return ResponseEntity.ok().body(Response.builder().result(true)
                .user(UserData.builder()
                        .id(authorizedUser.getId())
                        .email(authorizedUser.getEmail())
                        .name(authorizedUser.getName())
                        .photo(authorizedUser.getPhoto())
                        .moderationCount(0) //todo
                        .moderation(authorizedUser.isModerator())
                        .settings(authorizedUser.isModerator()).build()).build());
    }

    /**
     * Метод checkAndSendForgotPasswordMail.
     * Формирование и отправка письма с ссылкой для восстановления пароля.
     *
     * @param email - email для отправки.
     */
    public ResponseEntity<?> checkAndSendForgotPasswordMail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setForgotCode(UUID.randomUUID().toString());
            userRepository.save(user);
            emailService.send(user.getEmail(), messages.getMessage("rp.mail-subject"),
                    messages.getMessage(
                            "rp.message-text", user.getName(), createRestorePasswordLink(user.getForgotCode())
                    )
            );
            return ResponseEntity.ok().body(Response.builder().result(true).build());
        }
        return ResponseEntity.ok().body(Response.builder().result(false).build());
    }

    /**
     * Метод createRestorePasswordLink.
     * Формирование ссылки для восстановления пароля.
     *
     * @param forgotCode - токен для восстановления пароля.
     * @return ссылка для восстановления пароля.
     */
    private String createRestorePasswordLink(String forgotCode) {
        return new StringBuffer(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/login/change-password/").toUriString()).append(forgotCode).toString();
    }

    /**
     * Метод changePassword.
     * Изменение парооля пользователя.
     *
     * @param changePasswordRequest тело запроса на изменение пароля.
     * @param bindingResult         результаты валидации данных.
     */
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest,
                                            BindingResult bindingResult) {
        if (!captchaCodesService.captchaIsExist(changePasswordRequest.getCaptcha(),
                changePasswordRequest.getCaptcha_secret())) {
            bindingResult.addError(new FieldError(
                    "captcha", "captcha", messages.getMessage("cp.errors.captcha-code")));
        }
        User user = userRepository.findByForgotCode(changePasswordRequest.getCode()).orElse(null);
        if (user == null) {
            bindingResult.addError(new FieldError("code", "code",
                    messages.getMessage("cp.errors.forgot-code")));
        }
        if (bindingResult.hasErrors()) {
            return createResponseWithErrorMap(bindingResult);
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body(Response.builder().result(true).build());
    }

    /**
     * Метод createResponseWithErrorMap.
     * Создание ответа с перечнем ошибок валидации данных запроса.
     *
     * @param bindingResult - результаты валидации.
     * @return ResponseEntity.
     */
    private ResponseEntity<?> createResponseWithErrorMap(BindingResult bindingResult) {
        Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        if (errorMap.containsKey("e_mail")) {
            String message = errorMap.remove("e_mail");
            errorMap.put("email", message);
        }
        return ResponseEntity.ok().body(Response.builder().result(false).errors(errorMap).build());
    }

}
