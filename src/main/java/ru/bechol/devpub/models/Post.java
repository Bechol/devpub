package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "is_active", nullable = false)
    private boolean active;
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "moderated_by")
    private User moderatedBy;
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime time;
    @Column(nullable = false)
    private String title;
    @Column(name = "post_text", nullable = false)
    private String text;
    @Column(name = "view_count")
    private int viewCount;
    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Vote> votes;
    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Comment> comments;
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;


    public enum ModerationStatus {
        NEW, ACCEPTED, DECLINED
    }
}
