package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.models.Vote;
import ru.bechol.devpub.repository.VoteRepository;
import ru.bechol.devpub.request.PostIdRequest;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import java.security.Principal;
import java.util.Map;

/**
 * Класс VoteService.
 * Реализация сервисного слоя для Vote.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.Vote
 * @see VoteRepository
 */
@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    /**
     * Метод checkLikeVote.
     * Проверка был ли ранее поставлен лайк/дизлайк пользователем.
     *
     * @param post  - пост
     * @param user  - пользователь, который ставит лайк
     * @param value - 1:like, -1:dislike
     * @return - true если данный пользователь ранее ставил лайк.
     */
    public boolean isPostVoted(Post post, User user, int value) {
        return post.getVotes().stream()
                .filter(vote -> vote.getValue() == value).anyMatch(vote -> vote.getUser().equals(user));
    }

    /**
     * Метод like.
     * Лайк/дизлайк поста. Операция зависит от переданного значения value.
     *
     * @param postIdRequest - id поста.
     * @param principal     - авторизованный пользователь.
     * @param value         - 1:like, -1:dislike
     * @return Response.
     */
    public Map<String, Boolean> vote(PostIdRequest postIdRequest, Principal principal, int value) throws PostNotFoundException {
        User activeUser = userService.findByEmail(principal.getName());
        Post post = postService.findById(postIdRequest.getPostId());
        if (this.isPostVoted(post, activeUser, value)) {
            return Map.of("result", false);
        }
        Vote vote = new Vote();
        vote.setPost(post);
        vote.setUser(activeUser);
        vote.setValue(value);
        voteRepository.save(vote);
        voteRepository.deleteByPostAndUserAndValue(post, activeUser, value * -1);
        return Map.of("result", true);
    }
}
