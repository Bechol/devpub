package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.bechol.devpub.repository.IVoteRepository;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс Vote.
 * Доменный объект, представляющий лайк/дизлайк.
 *
 * @author Oleg Bech.
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see IVoteRepository
 * @see ru.bechol.devpub.service.IVoteService
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "post_votes")
public class Vote extends BaseEntity {

	@CreationTimestamp
	@Column(name = "time", nullable = false, columnDefinition = "timestamp")
	LocalDateTime time;
	int value;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_id")
	Post post;

}
