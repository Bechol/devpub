package ru.bechol.devpub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.service.CaptchaCodesService;
import ru.bechol.devpub.service.UserService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAuthControllerTest extends AbstractControllerTest {

    @Autowired
    private ApiAuthController apiAuthController;
    @MockBean
    private CaptchaCodesService captchaCodesService;
    @MockBean
    private UserService userService;

    @Test
    public void apiAuthControllerContextLoads() {
        assertThat(apiAuthController).isNotNull();
    }

    @Test
    public void whenGetCaptchaThenResponseIsOk() throws Exception {
        CaptchaResponse captchaResponse = CaptchaResponse.builder()
                .code("sdjnfs")
                .secret(UUID.randomUUID().toString()).build();
        Mockito.doReturn(ResponseEntity.ok().body(captchaResponse)).when(captchaCodesService).generateCaptcha();
        mockMvc.perform(get("/api/auth/captcha")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(captchaResponse.getCode())))
                .andExpect(content().string(containsString(captchaResponse.getSecret())))
                .andReturn();
    }

    @Test
    public void whenRegistrationFormDataInvalidThenGetResponseWithErrors() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setE_mail("mail.mail.ru");
        registerRequest.setName("");
        registerRequest.setCaptcha("asdb3n");
        registerRequest.setCaptcha_secret(UUID.randomUUID().toString());
        registerRequest.setPassword("pass");
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }


}
