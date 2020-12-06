package ru.bechol.devpub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.TagResponse;
import ru.bechol.devpub.service.TagService;

/**
 * Класс TagController.
 * REST контроллер для обычных запросов не через /api/tag/.
 *
 * @author Oleg Bech
 * @version 1.0
 */
@Tag(name = "/api/tag", description = "Работа с тегами постов")
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * Метод getTags.
     * GET запрос /api/tag.
     * Метод выдаёт список тегов, начинающихся на строку, заданную в параметре query.
     * В случае, если она не задана, выводятся все теги.
     *
     * @param query - строка запроса.
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
