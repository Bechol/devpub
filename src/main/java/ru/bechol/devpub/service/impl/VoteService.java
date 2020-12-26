package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.repository.IVoteRepository;
import ru.bechol.devpub.request.PostIdRequest;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.exception.PostNotFoundException;

import java.util.Map;

/**
 * Класс VoteService.
 * Реализация сервисного слоя для Vote.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.Vote
 * @see IVoteRepository
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class VoteService implements IVoteService {

	@Autowired
	IVoteRepository voteRepository;
	@Autowired
	@Qualifier("userService")
	private IUserService userService;
	@Autowired
	@Qualifier("postService")
	IPostService postService;
	@Autowired
	Messages messages;

	/**
	 * Метод like.
	 * Лайк/дизлайк поста. Операция зависит от переданного значения value.
	 *
	 * @param postIdRequest id поста.
	 * @param user          авторизованный пользователь.
	 * @param value         1:like, -1:dislike
	 * @return Response.
	 */
	@Override
	public ResponseEntity<?> vote(PostIdRequest postIdRequest, User user, int value) throws PostNotFoundException {
		Post post = postService.findById(postIdRequest.getPostId());
		if (this.isPostVoted(post, user, value)) {
			return ResponseEntity.ok(Map.of("result", false));
		}
		Vote vote = new Vote();
		vote.setPost(post);
		vote.setUser(user);
		vote.setValue(value);
		voteRepository.save(vote);
		voteRepository.deleteByPostAndUserAndValue(post, user, value * -1);
		return ResponseEntity.ok(Map.of("result", true));
	}

	/**
	 * Метод checkLikeVote.
	 * Проверка был ли ранее поставлен лайк/дизлайк пользователем.
	 *
	 * @param post  пост
	 * @param user  пользователь, который ставит лайк
	 * @param value 1:like, -1:dislike
	 * @return - true если данный пользователь ранее ставил лайк.
	 */
	private boolean isPostVoted(Post post, User user, int value) {
		return post.getVotes().stream()
				.filter(vote -> vote.getValue() == value).anyMatch(vote -> vote.getUser().equals(user));
	}
}
