package com.example.soundcloud.models.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    private String profileImageUrl;
    @OneToMany(mappedBy = "uploader")
    private List<Song> songs;



//    TODO to create OneToMany relationship for User-songs
//    TODO to create OneToMany relationship for User-Comments
//    TODO to create OneToMany relationship for User-Playlists
//    TODO to create ManyToMany relationship with Comments for Like/Dislike


    //TODO to create ManyToMany relationship with Songs for Like/Dislike
    @ManyToMany
    @JoinTable(
            name = "users_likes_songs",
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

}
