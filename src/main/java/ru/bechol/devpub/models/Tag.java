package ru.bechol.devpub.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;
}
