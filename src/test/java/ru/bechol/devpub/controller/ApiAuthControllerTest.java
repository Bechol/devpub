package ru.bechol.devpub.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import ru.bechol.devpub.response.CaptchaResponse;
import ru.bechol.devpub.service.CaptchaCodesService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAuthControllerTest extends AbstractControllerTest {

    @Autowired
    private ApiAuthController apiAuthController;
    @MockBean
    private CaptchaCodesService captchaCodesService;

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


}
