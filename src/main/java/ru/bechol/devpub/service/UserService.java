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
import ru.bechol.devpub.models.Role;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.RoleRepository;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.AuthorizationResponse;
import ru.bechol.devpub.response.RegistrationResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс UserService.
 * Реализация сервисного слоя для User.
 *
 * @author Oleg Bech
 * @version 1.0
 * @implements UserDetailsService.
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

    /**
     * Метод registrateNewUser.
     * Регистрация нового пользователя.
     *
     * @param registerRequest данные с формы, прошедшие валидацию.
     * @return ResponseEntity<RegistrationResponse>.
     */
    public ResponseEntity<RegistrationResponse> registrateNewUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getE_mail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setModerator(false);
        userRepository.save(setUserRole(user));
        return ResponseEntity.ok().body(RegistrationResponse.builder().result(true).build());
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
     * Метод registrateWithValidationErrors.
     * Формирование ответа с ошибками валидации.
     *
     * @param bindingResult результаты валидации данных с формы.
     * @return ResponseEntity<RegistrationResponse>.
     */
    public ResponseEntity<RegistrationResponse> registrateWithValidationErrors(BindingResult bindingResult) {
        Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        if (errorMap.containsKey("e_mail")) {
            String message = errorMap.remove("e_mail");
            errorMap.put("email", message);
        }
        return ResponseEntity.ok().body(RegistrationResponse.builder().result(false).errors(errorMap).build());
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

    public ResponseEntity<AuthorizationResponse> checkAuthorization(HttpServletRequest request) {
        ResponseEntity<AuthorizationResponse> falseResponse = ResponseEntity.ok().body(
                AuthorizationResponse.builder().result(false).build());
        if (sessionMap.isEmpty()) {
            return falseResponse;
        }
        Long userId = sessionMap.get(request.getSession().getId());
        if (userId == null) {
            return falseResponse;
        }
        User authorizedUser = userRepository.findById(userId).orElse(null);
        if (authorizedUser == null) {
            return falseResponse;
        }
        return ResponseEntity.ok().body(AuthorizationResponse.builder().result(true)
                .userData(AuthorizationResponse.UserData.builder()
                        .id(authorizedUser.getId())
                        .email(authorizedUser.getEmail())
                        .name(authorizedUser.getName())
                        .photo(authorizedUser.getPhoto())
                        .moderationCount(0) //todo количество постов на модерации
                        .moderation(authorizedUser.isModerator())
                        .settings(authorizedUser.isModerator()).build()).build());
    }
}
