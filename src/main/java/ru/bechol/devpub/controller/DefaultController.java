package ru.bechol.devpub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bechol.devpub.response.GeneralInfoResponse;
import ru.bechol.devpub.service.GeneralInfoService;

/**
 * Класс DefaultController.
 * REST контроллер для обычных запросов не через API (главная страница - /, в частности)
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see GeneralInfoService
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
public class DefaultController {

    @Autowired
    private GeneralInfoService generalInfoService;

    /**
     * Метод getGeneralInfo.
     * GET запрос /api/init.
     * @return общая информация о блоге в json формате.
     */
    @GetMapping("/init")
    private ResponseEntity<GeneralInfoResponse> getGeneralInfo() {
        return generalInfoService.getGeneralInfo();
    }
}
