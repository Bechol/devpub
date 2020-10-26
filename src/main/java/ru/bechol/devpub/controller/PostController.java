package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.PostsResponse;
import ru.bechol.devpub.service.PostService;

/**
 * Класс PostController.
 * REST контроллер для обычных запросов не через /api/post.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Метод allSorted.
     * GET запрос /api/post.
     * Метод получения постов со всей сопутствующей информацией для главной страницы и подразделов "Новые",
     * "Самые обсуждаемые", "Лучшие" и "Старые". Метод выводит посты, отсортированные в соответствии с параметром mode.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param mode   -  режим вывода (сортировка).
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping
    public ResponseEntity<PostsResponse> allSorted(@RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
        return postService.findAllSorted(offset, limit, mode);
    }
}
