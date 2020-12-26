package ru.bechol.devpub.service;

import org.springframework.http.ResponseEntity;
import ru.bechol.devpub.models.Tag;
import ru.bechol.devpub.response.TagResponse;

import java.util.List;

/**
 * Интерфейс ITagService.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 */
public interface ITagService {

	/**
	 * Метод findAllTagsByQuery.
	 * Поиск тегов по строке, значение которой содержится в наименовании тега.
	 *
	 * @param query строка запроса для выборки тегов.
	 * @return ResponseEntity<TagResponse>.
	 */
	ResponseEntity<TagResponse> findAllTagsByQuery(String query);

	/**
	 * Метод checkTags.
	 * Проверка существования тегов по наименованиям и создание новых тегов из полученной коллекции наименований.
	 *
	 * @param tagNames коллекция наименований новых тегов.
	 * @return - коллекция тегов
	 */
	public List<Tag> mapTags(List<String> tagNames);
}
