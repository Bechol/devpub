package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.request.NewPostRequest;
import ru.bechol.devpub.response.PostDto;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.PostService;

import javax.validation.Valid;
import java.security.Principal;

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
    @ResponseStatus(HttpStatus.OK)
    public PostResponse allSorted(@RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
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
    @ResponseStatus(HttpStatus.OK)
    public PostResponse searchByQuery(@RequestParam int offset, @RequestParam int limit,
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
    @ResponseStatus(HttpStatus.OK)
    public PostResponse searchByDate(@RequestParam int offset, @RequestParam int limit,
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
     * @param tag    -  тег, к которому привязан пост.
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping("/byTag")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse searchByTag(@RequestParam int offset, @RequestParam int limit,
                                    @RequestParam String tag) {
        return postService.findByTag(offset, limit, tag);
    }

    /**
     * Метод findMyPosts.
     * GET запрос /api/post/my.
     * Метод выводит только те посты, которые создал я (в соответствии с полем user_id в таблице posts базы данных).
     * Возможны 4 типа вывода (см. ниже описания значений параметра status).
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param status -  статус модерации.
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse findMyPosts(Principal user, @RequestParam int offset,
                                    @RequestParam int limit, @RequestParam String status) {
        return postService.findMyPosts(user, offset, limit, status);
    }

    /**
     * Метод createNewPost.
     * GET запрос /api/post/my.
     * Метод выводит только те посты, которые создал я (в соответствии с полем user_id в таблице posts базы данных).
     * Возможны 4 типа вывода (см. ниже описания значений параметра status).
     *
     * @param newPostRequest - json для создания поста.
     * @param bindingResult  - результат валидации данных нового поста.
     * @param principal      - авторизованный пользователь.
     * @return ResponseEntity<PostsResponse>.
     */
    @PostMapping
    public ResponseEntity<Response<?>> createNewPost(@Valid @RequestBody NewPostRequest newPostRequest,
                                                     BindingResult bindingResult, Principal principal) {
        return postService.createNewPost(principal, newPostRequest, bindingResult);
    }
}
