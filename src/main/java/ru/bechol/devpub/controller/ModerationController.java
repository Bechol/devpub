package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.request.ModerationRequest;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.UserService;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import java.security.Principal;

/**
 * Класс ModerationController.
 * REST контроллер для запросов не через /api/moderation.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Метод moderatePost.
     * POST запрос /api/moderation
     * @param moderationRequest - тело запроса.
     * @param principal - авторизованный пользователь.
     * @return - Response.
     */
    @PostMapping
    public Response moderatePost(@RequestBody ModerationRequest moderationRequest, Principal principal)
            throws PostNotFoundException {
        return postService.moderatePost(moderationRequest, principal);
    }
}
