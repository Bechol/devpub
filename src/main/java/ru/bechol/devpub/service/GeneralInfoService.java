package ru.bechol.devpub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.bechol.devpub.controller.DefaultController;
import ru.bechol.devpub.response.GeneralInfoResponse;

/**
 * Класс GeneralInfoService.
 * Работа с общей информацией о блоге.
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see DefaultController
 * @see GeneralInfoResponse
 * @version 1.0
 */
@Service
public class GeneralInfoService {

    @Autowired
    private GeneralInfoResponse generalInfoResponse;

    /**
     * Метод getGeneralInfo.
     * Чтение параметров из конфига для построения ответа на GET запрос /api/init.
     * @return ResponseEntity<GeneralInfoResponse>
     */
    public ResponseEntity<GeneralInfoResponse> getGeneralInfo() {
        return ResponseEntity.ok().body(generalInfoResponse);
    }
}
