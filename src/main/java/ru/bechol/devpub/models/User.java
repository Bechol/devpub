package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.bechol.devpub.repository.IUserRepository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс User.
 * Доменный объект, представляющий пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see IUserRepository
 * @see ru.bechol.devpub.service.IUserService
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

	@Column(nullable = false)
	boolean isModerator;
	@UpdateTimestamp
	@Column(name = "reg_time", columnDefinition = "timestamp", nullable = false)
	LocalDateTime regTime;
	@Column(nullable = false)
	String name;
	@Column(nullable = false)
	String email;
	@Column(nullable = false)
	String password;
	@Column(name = "code")
	String forgotCode;
	@Column(name = "photo")
	String photoLink;
	@Column(name = "photo_public_id")
	String photoPublicId;
	@JsonBackReference
	@OneToMany(mappedBy = "moderator")
	Set<Post> moderatedPosts;

	@JsonBackReference
	@OneToMany(mappedBy = "moderatedBy")
	Set<Post> moderatedByPosts;

	@JsonBackReference
	@OneToMany(mappedBy = "user")
	List<Post> posts;

	@JsonBackReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	Set<Vote> votes;

	@JsonManagedReference
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "role_id")}
	)
	List<Role> roles;

	public boolean isModerator() {
		return this.roles.stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR"));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
