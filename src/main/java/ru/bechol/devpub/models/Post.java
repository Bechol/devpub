package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс Post.
 * Доменный объект, представляющий пост.
 *
 * @author Oleg Bech.
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.PostRepository
 * @see ru.bechol.devpub.service.PostService
 * @see ru.bechol.devpub.controller.PostController
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

	@Column(name = "is_active", nullable = false)
	boolean active;
	@Column(name = "moderation_status")
	String moderationStatus;
	@Column(name = "time", nullable = false, columnDefinition = "timestamp")
	LocalDateTime time;
	@Column(nullable = false)
	String title;
	@Column(name = "post_text", nullable = false)
	String text;
	@Column(name = "view_count")
	int viewCount;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "moderator_id")
	User moderator;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "moderated_by")
	User moderatedBy;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;

	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	Set<Vote> votes;

	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	Set<Comment> comments;

	@JsonManagedReference
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name = "tag2post",
			joinColumns = {@JoinColumn(name = "post_id")},
			inverseJoinColumns = {@JoinColumn(name = "tag_id")})
	List<Tag> tags;
}
