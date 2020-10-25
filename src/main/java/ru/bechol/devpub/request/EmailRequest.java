package ru.bechol.devpub.request;

import lombok.Data;

import javax.validation.constraints.Email;

/**
 * Класс EmailRequest.
 * Ввод email пользователем на форме.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 */
@Data
public class EmailRequest {

    @Email
    private String email;
}
