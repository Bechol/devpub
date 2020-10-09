package ru.bechol.devpub.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DefaultControllerTest extends AbstractControllerTest {

    @Autowired
    private DefaultController defaultController;

    @Test
    public void defaultControllerContextLoads() {
        assertThat(defaultController).isNotNull();
    }

    @Test
    public void whenRequestApiInitThenResponseWithOk() throws Exception {
        mockMvc.perform(get("/api/init")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(phone)))
                .andExpect(content().string(containsString(email)))
                .andExpect(content().string(containsString(copyrightFrom)))
                .andReturn();
    }
}
