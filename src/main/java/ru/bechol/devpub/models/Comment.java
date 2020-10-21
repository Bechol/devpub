package ru.bechol.devpub.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "post_comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @CreationTimestamp
    @Column(name = "comment_time", nullable = false, columnDefinition = "timestamp with time zone")
    private LocalDateTime time;
    @Column(name = "comment_text", nullable = false)
    private String text;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Comment> children;
}
