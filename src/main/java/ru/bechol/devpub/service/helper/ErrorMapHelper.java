package ru.bechol.devpub.service.helper;

import org.springframework.http.*;
import org.springframework.validation.*;
import ru.bechol.devpub.response.Response;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс ErrorMapHelper.
 * Анализирует результат валидации и формирует ответ на запрос.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
public class ErrorMapHelper {
	/**
	 * Метод createBindingErrorResponse.
	 * Анализирует результат валидации и формирует ответ на запрос.
	 *
	 * @param bindingResult - результат валидации данных запроса.
	 * @param httpStatus    - статус ответа.
	 * @return - ответ на запрос.
	 */
	public static ResponseEntity<?> createBindingErrorResponse(BindingResult bindingResult, HttpStatus httpStatus) {
		Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
		return ResponseEntity.status(httpStatus).body(Response.builder().result(false).errors(errorMap).build());
	}
}
