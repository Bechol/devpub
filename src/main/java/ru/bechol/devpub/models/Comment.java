package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post_comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @CreationTimestamp
    @Column(name = "comment_time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime time;
    @Column(name = "comment_text", nullable = false)
    private String text;
    @JsonBackReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Comment> children;
}
