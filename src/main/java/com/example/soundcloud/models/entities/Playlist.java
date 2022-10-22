package com.example.soundcloud.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "playlists")
public class Playlist {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


//    @ManyToMany
//    @JoinTable(
//            name = "playlists_songs",
//            joinColumns = @JoinColumn(name = "playlists_id"),
//            inverseJoinColumns = @JoinColumn(name = "songs_id")
//    )
//    private List<Song> songsInPlaylist;


}
