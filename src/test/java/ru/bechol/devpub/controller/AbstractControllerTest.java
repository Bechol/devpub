package ru.bechol.devpub.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public abstract class AbstractControllerTest {

    @Value("${default.title}")
    String title;
    @Value("${default.subtitle}")
    String subtitle;
    @Value("${default.phone}")
    String phone;
    @Value("${default.email}")
    String email;
    @Value("${default.copyright}")
    String copyright;
    @Value("${default.copyrightFrom}")
    String copyrightFrom;

    @Autowired
    protected MockMvc mockMvc;
}

