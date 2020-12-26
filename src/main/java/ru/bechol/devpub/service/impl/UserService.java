package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.repository.*;
import ru.bechol.devpub.request.*;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.response.dto.UserDto;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.enums.*;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

import static ru.bechol.devpub.service.helper.ErrorMapHelper.createBindingErrorResponse;

/**
 * Класс UserService.
 * Реализация сервисного слоя для User.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.User
 * @see IUserRepository
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class UserService implements IUserService {

	final static String ROLE_USER = "ROLE_USER";
	final static Map<String, Boolean> RESULT_TRUE_MAP = Map.of("result", true);
	final static Map<String, Boolean> RESULT_FALSE_MAP = Map.of("result", false);

	@Autowired
	@Qualifier("globalSettingsService")
	IGlobalSettingsService globalSettingsService;
	@Autowired
	@Qualifier("emailService")
	IEmailService emailService;
	@Autowired
	@Qualifier("captchaCodesService")
	ICaptchaCodesService captchaCodesService;
	@Autowired
	@Qualifier("postService")
	IPostService postService;
	@Autowired
	@Qualifier("roleService")
	IRoleService roleService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	Messages messages;
	@Autowired
	IUserRepository userRepository;
	@Autowired
	IVoteRepository voteRepository;
	@Autowired
	IPostRepository postRepository;


	/**
	 * Метод registrateNewUser.
	 * Регистрация нового пользователя.
	 *
	 * @param registerRequest данные с формы, прошедшие валидацию.
	 * @return ResponseEntity<?>.
	 */
	@Override
	public ResponseEntity<?> registrateNewUser(RegisterRequest registerRequest, BindingResult bindingResult)
			throws Exception {
		if (globalSettingsService.checkSetting("MULTIUSER_MODE", SettingValue.NO)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messages.getMessage("multi-user.off"));
		}
		if (!captchaCodesService.captchaIsExist(registerRequest.getCaptcha(), registerRequest.getCaptcha_secret())) {
			bindingResult.addError(new FieldError(
					"captcha", "captcha", messages.getMessage("cp.errors.captcha-code")));
		}
		if (bindingResult.hasErrors()) {
			return createBindingErrorResponse(bindingResult, HttpStatus.OK);
		}
		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setName(registerRequest.getName());
		user.setModerator(false);
		this.setUserRole(user);
		userRepository.save(user);
		return ResponseEntity.ok(RESULT_TRUE_MAP);
	}

	/**
	 * Метод isUserNotExistByEmail.
	 * Проверяет, существует что пользователь с данным email-ом не существует.
	 *
	 * @param email - почта пользователя.
	 * @return true - если пользователь не существует.
	 */
	@Override
	public boolean isUserNotExistByEmail(String email) {
		return Strings.isNotEmpty(email) && userRepository.findByEmail(email).isEmpty();
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
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
				messages.getMessage("warning.user.not-found")));
	}

	/**
	 * Метод checkAuthorization.
	 * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
	 * Проверяет, сохранён ли идентификатор текущей сессии в списке авторизованных.
	 * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
	 * Считаются посты имеющие статус NEW и не проверерны модератором.
	 * Если пользователь не модератор возращать 0 в moderationCount.
	 *
	 * @param authorizedUser авторизованный пользователь.
	 * @return ResponseEntity.
	 */
	@Override
	public ResponseEntity<?> checkAuthorization(User authorizedUser) {
		if (authorizedUser == null) {
			return ResponseEntity.ok(RESULT_FALSE_MAP);
		}
		return ResponseEntity.ok().body(Response.builder().result(true)
				.user(UserDto.builder()
						.id(authorizedUser.getId())
						.email(authorizedUser.getEmail())
						.name(authorizedUser.getName())
						.photo(authorizedUser.getPhotoLink())
						.moderationCount(postService.findPostsByStatus(ModerationStatus.NEW))
						.moderation(authorizedUser.isModerator())
						.settings(authorizedUser.isModerator()).build()).build());
	}

	/**
	 * Метод checkAndSendForgotPasswordMail.
	 * Формирование и отправка письма с ссылкой для восстановления пароля.
	 *
	 * @param email         - email для отправки.
	 * @param bindingResult - результат валидации данных, ввуденых пользователем
	 */
	@Override
	public Map<String, Boolean> checkAndSendForgotPasswordMail(String email, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return RESULT_FALSE_MAP;
		}
		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) {
			user.setForgotCode(UUID.randomUUID().toString());
			userRepository.save(user);
			emailService.send(user.getEmail(), messages.getMessage("rp.mail-subject"),
					messages.getMessage(
							"rp.message-text", user.getName(), createRestorePasswordLink(user.getForgotCode())
					)
			);
			return RESULT_TRUE_MAP;
		}
		return RESULT_FALSE_MAP;
	}

	/**
	 * Метод changePassword.
	 * Изменение парооля пользователя.
	 *
	 * @param changePasswordRequest тело запроса на изменение пароля.
	 * @param bindingResult         результаты валидации данных.
	 */
	@Override
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
			return createBindingErrorResponse(bindingResult, HttpStatus.OK);
		}
		user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
		user.setForgotCode(null);
		userRepository.save(user);
		return ResponseEntity.ok().body(Map.of("result", true));
	}

	/**
	 * Метод setUserRole.
	 * Присвоение роли ROLE_USER вновь зарегистрированному пользователь.
	 *
	 * @param user пользователь.
	 * @return пользователь с роллью user.
	 */
	private void setUserRole(User user) throws RoleNotFoundException {
		user.setRoles(Collections.singletonList(roleService.findByName(ROLE_USER)));
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

}