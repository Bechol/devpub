package ru.bechol.devpub.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.response.UserData;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class ApplicationAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Map<String, Long> sessionMap;

    @Autowired
    public ApplicationAuthFilter(AuthenticationManager authenticationManager, Map<String, Long> sessionMap) {
        this.authenticationManager = authenticationManager;
        this.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/api/auth/login", "POST"));
        this.sessionMap = sessionMap;
    }

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

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();
        sessionMap.put(request.getSession().getId(), user.getId());
        response.getWriter().println(new ObjectMapper()
                .writeValueAsString(createLoginResponse(user, authResult)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.getWriter().println(new ObjectMapper()
                .writeValueAsString(Response.builder().result(false).build()));
    }

    private Response<?> createLoginResponse(User user, Authentication authenticationResult) {
        return Response.builder()
                .result(authenticationResult.isAuthenticated())
                .user(UserData.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .photo(user.getPhoto())
                        .email(user.getEmail())
                        .moderation(user.isModerator())
                        .moderationCount(0)
                        .settings(user.isModerator()).build()).build();
    }

    @Getter
    private static class UserCredentials {

        private String e_mail, password;

        String getEmail() {
            return e_mail;
        }
    }
}