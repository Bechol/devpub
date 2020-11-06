package ru.bechol.devpub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Класс User.
 * Реализация сущности пользователя.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.repository.UserRepository
 * @see ru.bechol.devpub.service.UserService
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private boolean isModerator;
    @UpdateTimestamp
    @Column(name = "reg_time", columnDefinition = "timestamp", nullable = false)
    private LocalDateTime regTime;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "code")
    private String forgotCode;
    @Column(name = "photo")
    private String photoLink;
    @Column(name = "photo_public_id")
    private String photoPublicId;
    @JsonBackReference
    @OneToMany(mappedBy = "moderator")
    private Set<Post> moderatedPosts;
    @JsonBackReference
    @OneToMany(mappedBy = "moderatedBy")
    private Set<Post> moderatedByPosts;
    @JsonBackReference
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Vote> votes;
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<Role> roles;

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
