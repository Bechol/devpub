package ru.bechol.devpub.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Класс ForwardController.
 * Контроллер для проброса запросов.
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @version 1.0
 */
@Controller
public class ForwardController {
    @RequestMapping(value = {
            "/edit/*",
            "/calendar/*",
            "/my/*",
            "/login",
            "/login/**",
            "/moderator/*",
            "/moderation/*",
            "/post/*",
            "/posts/*",
            "/profile",
            "settings",
            "/stat",
            "/404"
    })
    public String goForward() {
        return "forward:/";
    }
}
