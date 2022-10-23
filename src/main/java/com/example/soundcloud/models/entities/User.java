package com.example.soundcloud.models.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    public User(){

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
    @Column
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


//    TODO to create OneToMany relationship for User-songs - done
//    TODO to create OneToMany relationship for User-Comments
//    TODO to create OneToMany relationship for User-Playlists
//    TODO to create ManyToMany relationship with Comments for Like/Dislike


    //TODO to create ManyToMany relationship with Songs for Like/Dislike

}
