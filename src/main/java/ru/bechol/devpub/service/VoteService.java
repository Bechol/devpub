package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.models.Vote;
import ru.bechol.devpub.repository.PostRepository;
import ru.bechol.devpub.repository.VoteRepository;
import ru.bechol.devpub.request.PostIdRequest;
import ru.bechol.devpub.response.Response;

import java.security.Principal;

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
    private PostRepository postRepository;

    /**
     * Метод checkLikeVote.
     * Проверка был ли ранее поставлен лайк пользователем.
     *
     * @param post - пост
     * @param user - пользователь, который ставит лайк
     * @return - true если данный пользователь ранее ставил лайк.
     */
    public boolean isPostLikedByUser(Post post, User user) {
        return post.getVotes().stream()
                .filter(vote -> vote.getValue() == 1).anyMatch(vote -> vote.getUser().equals(user));
    }

    /**
     * Метод like.
     * Лайк поста.
     *
     * @param postIdRequest - id поста.
     * @param principal     - авторизованный пользователь.
     * @return Response.
     */
    public Response like(PostIdRequest postIdRequest, Principal principal) {
        User activeUser = userService.findByEmail(principal.getName()).orElse(null);
        if (activeUser == null) {
            return Response.builder().result(false).build();
        }
        Post post = postRepository.findById(postIdRequest.getPostId()).orElse(null);
        if (post == null || post.getUser().equals(activeUser) || this.isPostLikedByUser(post, activeUser)) {
            return Response.builder().result(false).build();
        }
        Vote vote = new Vote();
        vote.setPost(post);
        vote.setUser(activeUser);
        vote.setValue(1);
        voteRepository.save(vote);
        post.getVotes().stream().filter(dVote -> dVote.getValue() == -1).findFirst()
                .ifPresent(dVote -> voteRepository.delete(dVote));
        return Response.builder().result(true).build();
    }
}
