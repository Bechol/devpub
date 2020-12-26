package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.request.*;
import ru.bechol.devpub.response.*;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.impl.CaptchaCodesService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * Класс ApiAuthController.
 * REST контроллер.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see CaptchaCodesService
 * @see IUserService
 */
@Tag(name = "/api/auth", description = "Регистрация, авторизация и аутентификация пользователей")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

	@Autowired
	@Qualifier("captchaCodesService")
	ICaptchaCodesService captchaCodesService;
	@Autowired
	@Qualifier("userService")
	IUserService userService;

	/**
	 * Метод getCaptcha.
	 * GET запрос /api/auth/captcha
	 * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных (таблица captcha_codes) и
	 * возвращает секретный код secret (поле в базе данных secret_code) и изображение размером 100х35 с отображённым
	 * на ней основным кодом капчи image (поле базе данных code).
	 * Также удаляет устаревшие капчи из таблицы. Время устаревания задано в конфигурации приложения (по умолчанию, 1 час).
	 *
	 * @return ResponseEntity<CaptchaResponse>.
	 * @throws IOException
	 * @see CaptchaResponse
	 */
	@Operation(summary = "Генерация кодов капчи", description = "Метод генерирует коды капчи, - отображаемый и " +
			"секретный, - сохраняет их в базу данных (таблица captcha_codes) и возвращает секретный код secret " +
			"(поле в базе данных secret_code) и изображение размером 100х35 с отображённым на ней основным кодом " +
			"капчи image (поле базе данных code). Также метод должен удалять устаревшие капчи из таблицы. " +
			"Время устаревания должно быть задано в конфигурации приложения (по умолчанию, 1 час).")
	@GetMapping(value = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
	public CaptchaResponse getCaptcha() throws IOException {
		return captchaCodesService.generateCaptcha();
	}

	/**
	 * Метод register.
	 * POST запрос /api/auth/register
	 * Метод создаёт пользователя в базе данных, если введённые данные верны.
	 * Если данные неверные - пользователь не создаётся, а метод возвращает соответствующую ошибку.
	 *
	 * @param registerRequest данные пользовательской формы регистрации.
	 * @param bindingResult   результаты валидации данных пользовательской формы.
	 * @return ResponseEntity<?>.
	 */
	@Operation(summary = "Регистрация нового пользователя", description = "Метод создаёт пользователя в базе данных," +
			" если введённые данные верны. Если данные неверные - пользователь не создаётся, " +
			"а метод возвращает соответствующую ошибку.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = "{\n\t\"result\": true\n}")})
					}),
			@ApiResponse(responseCode = "400", description = "Ошибки при выполненении запроса",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
									"\t\t\"email\": \"Этот e-mail уже зарегистрирован\"," +
									"\t\t\"name\": \"Имя указано неверно\"," +
									"\t\t\"password\": \"Пароль короче 6-ти символов\"," +
									"\t\t\"captcha\": \"Код с картинки введён неверно\"\n}\n}")})
					}),
			@ApiResponse(responseCode = "404", description = "Регистрация новых пользователей выключена.",
					content = {@Content(examples = {
							@ExampleObject(value = "Регистрация сейчас невозможна. Попробуйте повторить позже.")})
					})
	})
	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
									  BindingResult bindingResult) throws Exception {
		return userService.registrateNewUser(registerRequest, bindingResult);
	}

	/**
	 * Метод check.
	 * GET запрос /api/auth/check
	 * Проверка авторизации пользователя.
	 *
	 * @return информация о текущем авторизованном пользователе, если он авторизован.
	 */
	@Operation(summary = "Проверка авторизации пользователя")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "v1", description = "Пользователь не авторизован",
									value = "{\n\t\"result\": false\n}"),
							@ExampleObject(name = "v2", description = "Пользователь авторизован",
									value = "{\n\t\"result\": true,\n\t\"user\": {\n" +
											"\t\t\"id\": 576," +
											"\t\t\"name\": \"Дмитрий Петров\"," +
											"\t\t\"photo\": \"ссылка на cloudinary\"," +
											"\t\t\"email\": \"petrov@petroff.ru\"," +
											"\t\t\"moderation\": true," +
											"\t\t\"moderationCount\": 56," +
											"\t\t\"settings\": true\n}\n}")
					}, schema = @Schema(implementation = Response.class))
					}),
	})

	@GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> check(@AuthenticationPrincipal User user) {
		return userService.checkAuthorization(user);
	}

	/**
	 * Метод restorePassword.
	 * POST запрос /api/auth/restore
	 * Метод проверяет наличие в базе пользователя с указанным e-mail.
	 * Если пользователь найден, ему отправляется письмо со ссылкой на восстановление пароля.
	 *
	 * @param emailRequest - email, на который необходимо выслать ссылку для восстановления.
	 */
	@Operation(summary = "Восстановление пароля", description = "Метод проверяет наличие в базе пользователя с " +
			"указанным e-mail. Если пользователь найден, ему должно отправляться письмо со ссылкой на восстановление " +
			"пароля следующего вида - /login/change-password/HASH, где HASH - сгенерированный код")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Новый пост сохранен",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "v1", description = "В случае, если логин найден " +
									"и ссылка восстановления отправлена", value = "{\n\t\"result\": true\n}"),
							@ExampleObject(name = "v2", description = "В случае, если логин не найден",
									value = "{\n\t\"result\": false\n}")
					})
					}),
	})
	@PostMapping("/restore")
	public Map<String, Boolean> restorePassword(@Valid @RequestBody EditProfileRequest emailRequest,
												BindingResult bindingResult) {
		return userService.checkAndSendForgotPasswordMail(emailRequest.getEmail(), bindingResult);
	}

	/**
	 * Метод changePassword.
	 * POST запрос /api/auth/password
	 * Метод проверяет корректность кода восстановления пароля (параметр code) и корректность кодов капчи:
	 * введённый код (параметр captcha) должен совпадать со значением в поле code таблицы captcha_codes,
	 * соответствующем пришедшему значению секретного кода
	 * (параметр captcha_secret и поле secret_code в таблице базы данных).
	 *
	 * @param changePasswordRequest - данные с пользовательской формы ввода.
	 */
	@Operation(summary = "Изменение пароля", description = "Метод проверяет корректность кода восстановления пароля " +
			"(параметр code) и корректность кодов капчи: введённый код (параметр captcha) должен совпадать со " +
			"значением в поле code таблицы captcha_codes, соответствующем пришедшему значению секретного кода " +
			"(параметр captcha_secret и поле secret_code в таблице базы данных).")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Новый пост сохранен",
					content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(name = "v1", description = "Если все данные отправлены верно",
									value = "{\n\t\"result\": true\n}"),
							@ExampleObject(name = "v2", description = "В случае, если запрос содержал ошибки",
									value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
											"\t\t\"code\": \"Ссылка для восстановления пароля устарела. Запросить ссылку снова\",\n" +
											"\t\t\"password\": \"Пароль короче 6-ти символов\",\n" +
											"\t\t\"captcha\": \"Код с картинки введён неверно\"\n}\n}")
					})
					}),
	})
	@PostMapping("/password")
	public ResponseEntity<?> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest, BindingResult bindingResult) {
		return userService.changePassword(changePasswordRequest, bindingResult);
	}

}
