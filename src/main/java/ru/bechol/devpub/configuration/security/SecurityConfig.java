package ru.bechol.devpub.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import ru.bechol.devpub.service.UserService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;
    @Autowired
    private UserService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.rememberMe().rememberMeServices(rememberMeServices()).key("devpub").and();
        http
                .csrf().disable()
                .addFilterBefore(new ApplicationAuthFilter(super.authenticationManagerBean()),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/*", "index", "/css/*", "/fonts/*", "/img/*", "/js/*").permitAll()
                .antMatchers("/api/auth/*").permitAll()
                .antMatchers("/api/*").permitAll()
                .antMatchers("/api/post/*").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout().logoutSuccessHandler(new AppLogoutHandler())
                .logoutUrl("/api/auth/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/");
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    @Bean
    public AbstractRememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices rememberMeServices =
                new PersistentTokenBasedRememberMeServices("devpub", userDetailsService, persistentTokenRepository());
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setCookieName("remember-me-devpub");
        rememberMeServices.setTokenValiditySeconds(86400);
        return rememberMeServices;
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
