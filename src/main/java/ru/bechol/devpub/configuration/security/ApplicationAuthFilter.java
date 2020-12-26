package ru.bechol.devpub.configuration.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.response.dto.UserDto;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.enums.ModerationStatus;

import javax.servlet.FilterChain;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Collections;

/**
 * Класс ApplicationAuthFilter.
 * Фильтр для обработки данных аутентификации пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationAuthFilter extends UsernamePasswordAuthenticationFilter {

	AuthenticationManager authenticationManager;
	IPostService postService;

	@Autowired
	public ApplicationAuthFilter(AuthenticationManager authenticationManager,
								 @Qualifier("postService") IPostService postService) {
		this.authenticationManager = authenticationManager;
		this.setRequiresAuthenticationRequestMatcher(
				new AntPathRequestMatcher("/api/auth/login", "POST"));
		this.postService = postService;
	}

	/**
	 * Метод attemptAuthentication.
	 * Попытка аутетунтификации пользователя.
	 *
	 * @param request  запрос на аутентификацию.
	 * @param response ответ сервера с результатом аутентификации.
	 * @return Authentication
	 * @throws AuthenticationException если пользователь не прошел аутентификацию.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			UserCredentials userCredentials = new ObjectMapper().readValue(
					request.getInputStream(), UserCredentials.class);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					userCredentials.getEmail(), userCredentials.getPassword(), Collections.emptyList());
			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Метод successfulAuthentication.
	 * Формирование ответа сервера в случае успешной аутентификации пользователя.
	 *
	 * @param request    запрос на аутентификацию.
	 * @param response   сформированный ответ сервера.
	 * @param chain      цепь фильтров.
	 * @param authResult результат аутентификации с данными пользователя.
	 * @throws IOException
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
											Authentication authResult) throws IOException {
		User user = (User) authResult.getPrincipal();
		response.getWriter().println(new ObjectMapper()
				.writeValueAsString(createLoginResponse(user, authResult)));
		SecurityContextHolder.getContext().setAuthentication(authResult);
	}

	/**
	 * Метод unsuccessfulAuthentication.
	 * Формирование ответа сервера в случае неудачной аутентификации пользователя.
	 *
	 * @param request  запрос на аутентификацию.
	 * @param response сформированный ответ сервера.
	 * @param failed   исключение, возникшее в результате проверки данных пользователя.
	 * @throws IOException
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  AuthenticationException failed) throws IOException {
		response.getWriter().println(new ObjectMapper()
				.writeValueAsString(Response.builder().result(false).build()));
	}

	/**
	 * Метод createLoginResponse.
	 * Конвертация результата аутентификации в ответ сервера.
	 *
	 * @param user                 авторизованный пользователь.
	 * @param authenticationResult данные аутентификации.
	 * @return объект для сериализации в Json.
	 */
	private Response<?> createLoginResponse(User user, Authentication authenticationResult) {
		return Response.builder()
				.result(authenticationResult.isAuthenticated())
				.user(UserDto.builder()
						.id(user.getId())
						.name(user.getName())
						.photo(user.getPhotoLink())
						.email(user.getEmail())
						.moderation(user.isModerator())
						.moderationCount(user.isModerator() ? postService.findPostsByStatus(ModerationStatus.NEW) : 0)
						.settings(user.isModerator()).build()).build();
	}

	/**
	 * Внутсренний статический класс UserCredentials.
	 * Служит для десериализации данных, введеных пользователем на форме аутентификации.
	 *
	 * @author Oleg Bech
	 * @version 1.0
	 * @email oleg071984@gmail.com
	 */
	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	private static class UserCredentials {

		@JsonProperty("e_mail")
		String email;
		String password;
	}
}
