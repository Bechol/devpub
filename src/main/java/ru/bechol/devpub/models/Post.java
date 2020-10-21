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
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="is_active", nullable = false)
    private boolean active;
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @CreationTimestamp
    @Column(name = "time", nullable = false, columnDefinition = "timestamp with time zone")
    private LocalDateTime time;
    @Column(nullable = false)
    private String title;
    @Column(name = "post_text", nullable = false)
    private String text;
    @Column(name = "view_count")
    private int viewCount;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Vote> votes;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Comment> comments;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private Set<Tag> tags;


    private enum ModerationStatus {
        NEW, ACCEPTED, DECLINED
    }
}
