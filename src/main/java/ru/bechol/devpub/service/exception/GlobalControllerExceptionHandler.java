package ru.bechol.devpub.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bechol.devpub.response.ErrorResponse;
import ru.bechol.devpub.service.Messages;
import ru.bechol.devpub.service.exception.*;

import javax.management.relation.RoleNotFoundException;
import java.nio.file.InvalidPathException;

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

    /**
     * Метод handleMissingParameter.
     * Обработка исключения MissingServletRequestParameterException.
     *
     * @param exception - MissingServletRequestParameterException.
     * @return - ResponseEntity<ErrorResponse>.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException exception) {
        return this.createErrorResponse("warning.required-not-present",
                exception.getParameterType(), "request parameter", exception.getParameterName());
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
        return this.createErrorResponse("warning.required-not-present",
                "path variable", exception.getVariableName(), "null");
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
        return this.createErrorResponse("warning.not-found-by", "user",
                exception.getField(), exception.getFieldValue());
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
        return this.createErrorResponse("warning.not-found-by", "role", "name", null);
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
        return this.createErrorResponse("warning.not-found", "post");
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
        return this.createErrorResponse("warning.not-found-by", "global setting", "code", null);
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
        return this.createErrorResponse("warning.not-found", "moderator");
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
