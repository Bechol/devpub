package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.response.StatisticResponse;
import ru.bechol.devpub.service.exception.CodeNotFoundException;

@Service
public interface IStatisticService {

	/**
	 * Метод calculateMyStatistics.
	 * Статистика по постам авторизованного пользователя.
	 *
	 * @param user авторизованный пользователь.
	 * @return StatisticResponse
	 */
	StatisticResponse calculateMyStatistics(User user);


	/**
	 * Метод calculateSiteStatistics.
	 * Статистика по всем постам блога.
	 *
	 * @param user авторизованный пользователь.
	 * @return ResponseEntity<?>
	 */
	ResponseEntity<?> calculateSiteStatistics(User user) throws CodeNotFoundException;
}
