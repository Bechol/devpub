package ru.bechol.devpub.configuration.security;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.bechol.devpub.service.*;

/**
 * Класс SecurityConfig.
 * Конфигурация Spring Security.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	@Qualifier("userService")
	IUserService userDetailsService;
	@Autowired
	@Qualifier("postService")
	IPostService postService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.csrf().disable()
				.addFilterBefore(new ApplicationAuthFilter(super.authenticationManagerBean(), postService),
						UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/**").permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.logout().logoutSuccessHandler(new AppLogoutHandler())
				.logoutUrl("/api/auth/logout")
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

}
