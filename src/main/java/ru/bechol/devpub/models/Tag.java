package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * Класс Tag.
 * Реализация тега.
 *
 * @author Oleg Bech.
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.TagRepository
 * @see ru.bechol.devpub.service.TagService
 * @see ru.bechol.devpub.service.PostService
 * @see ru.bechol.devpub.controller.TagController
 * @see ru.bechol.devpub.controller.PostController
 */
@Getter
@Setter
@Entity
@Table(name = "tags")
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @JsonBackReference
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;

    public Tag(String name) {
        this.name = name;
    }
}
