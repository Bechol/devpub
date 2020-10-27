package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping
    public ResponseEntity<TagResponse> getTags(@RequestParam(required = false) String query) {
        return tagService.findAllTagsByQuery(query);
    }
}
