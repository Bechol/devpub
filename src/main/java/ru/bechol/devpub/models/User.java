package ru.bechol.devpub.models;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс User.
 * Реализация сущности пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.UserRepository
 * @see ru.bechol.devpub.service.UserService
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private boolean isModerator;
    @UpdateTimestamp
    @Column(name = "reg_time", columnDefinition = "timestamp with time zone", nullable = false)
    private LocalDateTime regTime;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "code")
    private String forgotCode;
    @Column(name = "photo")
    private String photoLink;
}
