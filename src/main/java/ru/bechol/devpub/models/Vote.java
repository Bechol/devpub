package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post_votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @CreationTimestamp
    @Column(name = "time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime time;
    private int value;
}
