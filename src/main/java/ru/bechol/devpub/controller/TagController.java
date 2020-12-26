package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.bechol.devpub.response.TagResponse;
import ru.bechol.devpub.service.ITagService;

/**
 * Класс TagController.
 * REST контроллер.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@Tag(name = "/api/tag", description = "Работа с тегами постов")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/tag")
public class TagController {

	@Autowired
	@Qualifier("tagService")
	ITagService tagService;

	/**
	 * Метод getTags.
	 * GET запрос /api/tag.
	 * Метод выдаёт список тегов, начинающихся на строку, заданную в параметре query.
	 * В случае, если она не задана, выводятся все теги.
	 *
	 * @param query строка запроса.
	 * @return ResponseEntity<TagResponse>.
	 */
	@Operation(summary = "Cписок тэгов, начинающихся на строку, заданную в параметре query.",
			description = "Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре query. " +
					"В случае, если она не задана, выводятся все тэги. В параметре weight должен быть указан " +
					"относительный нормированный вес тэга от 0 до 1, соответствующий частоте его встречаемости. " +
					"Значение 1 означает, что этот тэг встречается чаще всего. Пример значений weight для разных " +
					"частот встречаемости:")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagResponse> getTags(@RequestParam(required = false) String query) {
		return tagService.findAllTagsByQuery(query);
	}
}
