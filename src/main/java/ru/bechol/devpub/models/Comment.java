package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс Comment.
 * Доменный объект, представляющий комментарий.
 *
 * @author Oleg Bech
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.CommentRepository
 * @see ru.bechol.devpub.service.CommentService
 * @see ru.bechol.devpub.controller.CommentController
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_comments")
public class Comment extends BaseEntity {

	@CreationTimestamp
	@Column(name = "comment_time", nullable = false, columnDefinition = "timestamp")
	LocalDateTime time;
	@Column(name = "comment_text", nullable = false)
	String text;

	@OneToOne
	@JoinColumn(name = "parent_id")
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	Comment parent;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	Post post;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	User user;


}
