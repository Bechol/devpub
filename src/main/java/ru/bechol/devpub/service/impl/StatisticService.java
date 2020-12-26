package ru.bechol.devpub.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.repository.*;
import ru.bechol.devpub.response.StatisticResponse;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.enums.SettingValue;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

import java.time.ZoneId;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class StatisticService implements IStatisticService {

	@Autowired
	IPostRepository postRepository;
	@Autowired
	IVoteRepository voteRepository;
	@Autowired
	@Qualifier("globalSettingsService")
	IGlobalSettingsService globalSettingsService;
	@Autowired
	@Qualifier("postService")
	IPostService postService;

	/**
	 * Метод calculateMyStatistics.
	 * Статистика по постам авторизованного пользователя.
	 *
	 * @param user авторизованный пользователь.
	 * @return StatisticResponse
	 */
	@Override
	public StatisticResponse calculateMyStatistics(User user) {
		List<Post> postList = postRepository.findByUserAndActiveTrue(user);
		return this.createStatisticsResponse(postList);
	}

	/**
	 * Метод calculateSiteStatistics.
	 * Статистика по всем постам блога.
	 *
	 * @return ResponseEntity<?>
	 */
	@Override
	public ResponseEntity<?> calculateSiteStatistics(User user) throws CodeNotFoundException {
		if (Objects.nonNull(user) && globalSettingsService.checkSetting("STATISTICS_IS_PUBLIC", SettingValue.NO)
				&& !user.isModerator()) {
			return ResponseEntity.status(401).build();
		}
		return ResponseEntity.ok(this.createStatisticsResponse(postService.findAll()));
	}

	/**
	 * Метод createStatisticsResponse.
	 * Создание отчета по статистике.
	 *
	 * @return StatisticResponse.
	 */
	private StatisticResponse createStatisticsResponse(List<Post> postList) {
		postList.sort(Comparator.comparing(Post::getTime));
		List<Vote> postsVotes = voteRepository.findByPostIn(postList);
		long postsCount = postList.size();
		long likesCount = postsVotes.stream().filter(vote -> vote.getValue() == 1).count();
		long dislikesCount = postsVotes.stream().filter(vote -> vote.getValue() == -1).count();
		int viewsCount = postList.stream().map(Post::getViewCount).reduce(Integer::sum).orElse(0);
		return StatisticResponse.builder()
				.postsCount(postsCount)
				.likesCount(likesCount)
				.dislikesCount(dislikesCount)
				.viewsCount(viewsCount)
				.firstPublication(!postList.isEmpty() ?
						postList.get(0).getTime().atZone(ZoneId.systemDefault()).toEpochSecond() : 0)
				.build();
	}
}
