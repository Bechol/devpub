package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.response.GeneralInfoResponse;
import ru.bechol.devpub.service.*;

import java.util.Map;

/**
 * Класс DefaultController.
 * REST контроллер.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see IGlobalSettingsService
 */
@Tag(name = "/api", description = "Общая информация о блоге, глобальные настройки")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api")
public class DefaultController {

	@Autowired
	GeneralInfoResponse generalInfoResponse;
	@Autowired
	@Qualifier("globalSettingsService")
	IGlobalSettingsService globalSettingsService;

	/**
	 * Метод getGeneralInfo.
	 * GET запрос /api/init.
	 * Возвращает общую информацию о блоге: название блога и подзаголовок для размещения в хэдере сайта,
	 * а также номер телефона, e-mail и информацию об авторских правах для размещения в футере.
	 *
	 * @return общая информация о блоге в json формате.
	 */
	@Operation(summary = "Общая информацию о блоге", description = "Возвращает название блога и " +
			"подзаголовок для размещения в хэдере сайта, а также номер телефона, e-mail и информацию об авторских " +
			"правах для размещения в футере.")
	@ApiResponse(responseCode = "200")
	@GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralInfoResponse> getGeneralInfo() {
		return ResponseEntity.ok(generalInfoResponse);
	}

	/**
	 * Метод getGeneralSettings.
	 * GET запрос /api/settings
	 * Возвращает глобальные настройки блога из таблицы global_settings.
	 *
	 * @return глобальные настройки блога из таблицы global_settings.
	 */
	@Operation(summary = "Возвращает глобальные настройки блога")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Глобальные настройки найдены в базе.",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(description = "Может быть одна или несколько настроек",
									value = "{\n\t\"MULTIUSER_MODE\": true," +
											"\n\t\"POST_PREMODERATION\": false," +
											"\n\t\"STATISTICS_IS_PUBLIC\": true\n}"),
					}, schema = @Schema(implementation = Map.class))
					}),
			@ApiResponse(responseCode = "204", description = "Настройки не найдены в базе",
					content = {@Content(schema = @Schema(hidden = true))}),
	})
	@GetMapping(value = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Boolean>> getGeneralSettings() {
		return globalSettingsService.createGeneralSettingsMap();
	}

	/**
	 * Метод updateGeneralSettings.
	 * PUT запрос /api/settings
	 * Метод записывает глобальные настройки блога в таблицу global_settings,
	 * если запрашивающий пользователь авторизован и является модератором.
	 *
	 * @return глобальные настройки блога из таблицы global_settings.
	 */
	@Operation(summary = "Запись глобальных настроек блога, если запрашивающий пользователь авторизован " +
			"и является модератором. Tребует авторизации")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Новые значения настроек успешно записаны",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "400", description = "Если авторизованный пользователь не является модератором",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
					content = {@Content(schema = @Schema(hidden = true))}),
			@ApiResponse(responseCode = "404", description = "Ни одна из указанных в теле запроса настроек не найдена",
					content = {@Content(schema = @Schema(hidden = true))}),
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Перечень настроек, которые необходимо изменить",
			required = true, content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(description = "Может быть одна или несколько настроек",
							value = "{\n\t\"MULTIUSER_MODE\": true," +
									"\n\t\"POST_PREMODERATION\": false," +
									"\n\t\"STATISTICS_IS_PUBLIC\": true\n}"),
			})
	})
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@PutMapping(value = "/settings",
			produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateGeneralSettings(@RequestBody Map<String, Boolean> settings) {
		return globalSettingsService.updateGeneralSettings(settings);
	}
}
