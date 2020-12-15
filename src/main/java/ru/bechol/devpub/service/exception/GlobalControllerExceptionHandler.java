package ru.bechol.devpub.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import ru.bechol.devpub.response.*;
import ru.bechol.devpub.service.Messages;

import javax.management.relation.RoleNotFoundException;
import java.nio.file.InvalidPathException;
import java.util.Map;

/**
 * Класс GlobalControllerExceptionHandler.
 * Обработка исключений, возникающих при выполнении запросов.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @Autowired
    private Messages messages;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    /**
     * Метод handleMissingParameter.
     * Обработка исключения MissingServletRequestParameterException.
     *
     * @param exception - MissingServletRequestParameterException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException exception) {
        return this.createErrorResponse("warning.request-parameter.not-present");
    }

    /**
     * Метод handlePathVariableException.
     * Обработка исключения MissingPathVariableException.
     *
     * @param exception - MissingPathVariableException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handlePathVariableException(MissingPathVariableException exception) {
        return this.createErrorResponse("warning.path-variable.not-present");
    }

    /**
     * Метод handleUsernameNotFoundException.
     * Обработка исключения UserNotFoundException.
     *
     * @param exception - UsernameNotFoundException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UserNotFoundException exception) {
        return this.createErrorResponse("warning.user.not-found");
    }

    /**
     * Метод handleRoleNotFoundException.
     * Обработка исключения RoleNotFoundException.
     *
     * @param exception - RoleNotFoundException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException exception) {
        return this.createErrorResponse("warning.role.not-found");
    }

    /**
     * Метод handlePostNotFoundException.
     * Обработка исключения PostNotFoundException.
     *
     * @param exception - PostNotFoundException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException exception) {
        return this.createErrorResponse("warning.post.not-found");
    }

    /**
     * Метод handleCodeNotFoundException.
     * Обработка исключения CodeNotFoundException.
     *
     * @param exception - CodeNotFoundException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(CodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCodeNotFoundException(CodeNotFoundException exception) {
        return this.createErrorResponse("warning.code.not-found");
    }

    /**
     * Метод handleInvalidPathException.
     * Обработка исключения InvalidPathException.
     *
     * @param exception - CodeNotFoundException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(InvalidPathException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPathException(InvalidPathException exception) {
        return this.createErrorResponse("bad.file-patch");
    }

    @ExceptionHandler(ModeratorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleModeratorNotFoundException(ModeratorNotFoundException exception) {
        return this.createErrorResponse("warning.moderator.not-found");
    }

    /**
     * Метод handleMultipartException.
     * Обработка исключения MultipartException, а именно FileSizeLimitExceededException, возникающего при загрузке
     * файла, размер которого, превышает максимально допустимый.
     * @param exception - перехваченное исключение.
     * @return bad_request с мапой ошибок.
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(MultipartException exception) {
        log.error(exception.getLocalizedMessage());
        return ResponseEntity.badRequest().body(Response.builder().result(false)
                .errors(Map.of("image-size", messages.getMessage("er.image-size", maxFileSize)))
                .build()
        );
    }

    /**
     * Метод createErrorResponse.
     * Запись в лог о возникшем исключении и создание ответа на запрос.
     *
     * @param messageId - id формулировки сообщения.
     * @param params    - набор параметров для сообщения.
     * @return - ResponseEntity<ErrorResponse>.
     */
    private ResponseEntity<ErrorResponse> createErrorResponse(String messageId, Object... params) {
        String errorMessage = messages.getMessage(messageId, params);
        log.error(errorMessage);
        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(errorMessage).build());
    }

}
