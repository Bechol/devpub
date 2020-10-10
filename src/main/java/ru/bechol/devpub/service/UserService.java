package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.RegistrationResponse;

import java.util.Map;
import java.util.Optional;
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
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
        user.setPassword(registerRequest.getPassword());
        user.setName(registerRequest.getName());
        user.setModerator(false);
        userRepository.save(user);
        return ResponseEntity.ok().body(RegistrationResponse.builder().result(true).build());
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
}
