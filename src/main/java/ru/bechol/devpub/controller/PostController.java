package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.request.NewPostRequest;
import ru.bechol.devpub.request.PostIdRequest;
import ru.bechol.devpub.response.PostDto;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.VoteService;

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
    @Autowired
    private VoteService voteService;

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

    /**
     * Метод showPost.
     * GET запрос /api/post/{id}.
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
     * список комментариев и тэгов, привязанных к данному посту. Выводит пост в любом случае, если пост активен
     * (параметр is_active в базе данных равен 1), принят модератором (параметр moderation_status равен ACCEPTED) и
     * время его публикации (поле timestamp) равно текущему времени или меньше его.
     *
     * @param postId    - id поста.
     * @param principal - авторизованный пользователь.
     * @return ResponseEntity<Response>.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> showPost(@PathVariable(name = "id") long postId, Principal principal) {
        return postService.showPost(postId, principal);
    }

    /**
     * Метод postsOnModerationэ
     * GET запрос /api/post/moderation
     *
     * @param offset    - сдвиг от 0 для постраничного вывода.
     * @param limit     - количество постов, которое надо вывести.
     * @param status    -  статус модерации.
     * @param principal - авторизованный пользователь
     * @return - PostResponse.
     */
    @GetMapping("/moderation")
    public PostResponse postsOnModeration(@RequestParam int offset, @RequestParam int limit,
                                          @RequestParam String status, Principal principal) {
        return postService.findPostsOnModeration(principal, offset, limit, status);
    }

    /**
     * Метод likePost
     * POST запрос /api/post/like
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя.
     * В случае повторного лайка возвращает {result: false}. Если до этого этот же пользователь поставил на этот
     * же пост дизлайк, этот дизлайк должен быть заменен на лайк в базе данных.
     *
     * @param postIdRequest - id поста для лайка.
     * @param principal     - авторизованный пользователь
     * @return - Response.
     */
    @PostMapping("/like")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> likePost(@RequestBody PostIdRequest postIdRequest, Principal principal) {
        return voteService.vote(postIdRequest, principal, 1);
    }

    /**
     * Метод dislikePost
     * POST запрос /api/post/dislike
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя.
     * В случае повторного дизлайка возвращает {result: false}.
     * Если до этого этот же пользователь поставил на этот же пост лайк,
     * этот лайк должен заменен на дизлайк в базе данных.
     *
     * @param postIdRequest - id поста для лайка.
     * @param principal     - авторизованный пользователь
     * @return - Response.
     */
    @PostMapping("/dislike")
    @ResponseStatus(HttpStatus.OK)
    public Response<?> dislikePost(@RequestBody PostIdRequest postIdRequest, Principal principal) {
        return voteService.vote(postIdRequest, principal, -1);
    }
}
