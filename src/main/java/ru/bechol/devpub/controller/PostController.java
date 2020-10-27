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

    /**
     * Метод searchByQuery.
     * GET запрос /api/post/search.
     * Метод возвращает посты, соответствующие поисковому запросу - строке query.
     * В случае, если запрос пустой, метод должен выводить все посты.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param query  -  поисковый запрос.
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping("/search")
    public ResponseEntity<PostsResponse> searchByQuery(@RequestParam int offset, @RequestParam int limit,
                                                       @RequestParam String query) {
        return postService.findByQuery(offset, limit, query);
    }

    /**
     * Метод searchByDate.
     * GET запрос /api/post/byDate.
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param date   -  дата в формате "YYYY-MM-dd"
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> searchByDate(@RequestParam int offset, @RequestParam int limit,
                                                      @RequestParam String date) {
        return postService.findByDate(offset, limit, date);
    }

    /**
     * Метод searchByTag.
     * GET запрос /api/post/byDate.
     * Метод выводит список постов, привязанных к тегу, который был передан методу в качестве параметра tag.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param tag   -  тег, к которому привязан пост.
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> searchByTag(@RequestParam int offset, @RequestParam int limit,
                                                      @RequestParam String tag) {
        return postService.findByTag(offset, limit, tag);
    }
}
