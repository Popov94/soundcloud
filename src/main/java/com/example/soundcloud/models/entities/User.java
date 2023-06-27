package com.example.soundcloud.models.entities;


import com.example.soundcloud.models.entities.listeners.Listened;
import com.example.soundcloud.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    public User(){

    }

    public User(String username, String password, String confirmPassword, String email, LocalDate dateOfBirthday,
                LocalDateTime createdAt, String gender) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.dateOfBirthday = dateOfBirthday;
        this.createdAt = createdAt;
        this.gender = gender;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String password;
    @Transient
    private String confirmPassword;
    @Column(name = "email")
    private String email;
    @Column(name = "date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate dateOfBirthday;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Column
    private String gender;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;
    @Column
    private String address;
    @Column
    private String city;
    @Column
    private String country;
    @Column(name = "verified")
    private boolean isVerified;
    @Column
    private String verificationCode;
    @Column
    private String profileImageUrl;
    @OneToMany(mappedBy = "uploader")
    private List<Song> songs;
    @ManyToMany
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> following;
    @ManyToMany(mappedBy = "following")
    private List<User> followers;
    @OneToMany(mappedBy = "playlistOwner")
    private List<Playlist> playlists;
    @OneToMany(mappedBy = "commentOwner")
    private List<Comment> comments;
    @ManyToMany
    @JoinTable(
            name = "users_like_songs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    List<Song> likedSongs;
    @ManyToMany
    @JoinTable(
            name = "users_dislike_songs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    List<Song> dislikedSongs;

    @ManyToMany
    @JoinTable(
            name = "users_like_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    List<Comment> likedComments;
    @ManyToMany
    @JoinTable(
            name = "users_dislike_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    List<Comment> dislikedComments;
    @OneToMany(mappedBy = "user")
    private List<Listened> listeners;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getUsername() {
        return email;
    }

    public String getPassword() {
        return password;
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
