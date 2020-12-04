package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.request.PostIdRequest;
import ru.bechol.devpub.request.PostRequest;
import ru.bechol.devpub.response.PostDto;
import ru.bechol.devpub.response.PostResponse;
import ru.bechol.devpub.service.PostService;
import ru.bechol.devpub.service.VoteService;
import ru.bechol.devpub.service.enums.ModerationStatus;
import ru.bechol.devpub.service.enums.PostStatus;
import ru.bechol.devpub.service.enums.SortMode;
import ru.bechol.devpub.service.exception.*;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

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
     * Метод getAllPostsSorted.
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
    public ResponseEntity<PostResponse> getAllPostsSorted(@RequestParam(defaultValue = "0") int offset,
                                                          @RequestParam(defaultValue = "20") int limit,
                                                          @RequestParam String mode) throws SortModeNotFoundException {
        return postService.findAllPostsSorted(offset, limit, SortMode.fromValue(mode));
    }

    /**
     * Метод findPostsByTextContainingQuery.
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
    public ResponseEntity<PostResponse> findPostsByTextContainingQuery(@RequestParam(defaultValue = "0") int offset,
                                                                       @RequestParam(defaultValue = "20") int limit,
                                                                       @RequestParam String query) {
        return postService.findPostsByTextContainingQuery(offset, limit, query);
    }

    /**
     * Метод searchPostsByDate.
     * GET запрос /api/post/byDate.
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     *
     * @param offset - сдвиг от 0 для постраничного вывода.
     * @param limit  - количество постов, которое надо вывести.
     * @param date   -  дата в формате "YYYY-MM-dd"
     * @return ResponseEntity<PostsResponse>.
     */
    @GetMapping("/byDate")
    public ResponseEntity<PostResponse> findPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "20") int limit,
                                                        @RequestParam String date) {
        return postService.findPostsByDate(offset, limit, date);
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
    public ResponseEntity<PostResponse> findByTag(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "20") int limit,
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
    public PostResponse findActiveUserPosts(Principal user, @RequestParam int offset, @RequestParam int limit,
                                            @RequestParam String status) throws PostStatusNotFoundException {
        return postService.findActiveUserPosts(user, offset, limit, PostStatus.fromValue(status));
    }

    /**
     * Метод createNewPost.
     * GET запрос /api/post/my.
     * Метод выводит только те посты, которые создал я (в соответствии с полем user_id в таблице posts базы данных).
     * Возможны 4 типа вывода (см. ниже описания значений параметра status).
     *
     * @param postRequest   - json для создания поста.
     * @param bindingResult - результат валидации данных нового поста.
     * @param principal     - авторизованный пользователь.
     * @return ResponseEntity<PostsResponse>.
     */
    @PostMapping
    public ResponseEntity<?> createNewPost(@Valid @RequestBody PostRequest postRequest,
                                           BindingResult bindingResult, Principal principal)
            throws RoleNotFoundException, CodeNotFoundException {
        return postService.createNewPost(principal, postRequest, bindingResult);
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
    public ResponseEntity<PostDto> showPost(@PathVariable(name = "id") long postId, Principal principal)
            throws PostNotFoundException {
        return postService.showPost(postId, principal);
    }

    /**
     * Метод editPost.
     * PUT запрос /api/post/{id}.
     * Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму публикации.
     *
     * @param editPostRequest - тело запроса.
     * @param bindingResult   - результаты валидации данных пользовательской формы.
     * @param postId          - id поста.
     * @param principal       - авторизованный пользователь.
     * @return ResponseEntity<Response>.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editPost(@Valid @RequestBody PostRequest editPostRequest, BindingResult bindingResult,
                                      @PathVariable("id") long postId, Principal principal) throws PostNotFoundException {
        return postService.editPost(editPostRequest, postId, principal, bindingResult);
    }


    /**
     * Метод postsOnModeration
     * Метод фиксирует действие модератора по посту: его утверждение или отклонение.
     * Кроме того, фиксируется moderator_id - идентификатор пользователя, который отмодерировал пост.
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
                                          @RequestParam String status, Principal principal)
            throws ModerationStatusNotFoundException {
        return postService.findPostsOnModeration(principal, offset, limit, ModerationStatus.fromValue(status));
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
    public Map<String, Boolean> likePost(@RequestBody PostIdRequest postIdRequest, Principal principal)
            throws PostNotFoundException {
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
    public Map<String, Boolean> dislikePost(@RequestBody PostIdRequest postIdRequest, Principal principal)
            throws PostNotFoundException {
        return voteService.vote(postIdRequest, principal, -1);
    }
}
