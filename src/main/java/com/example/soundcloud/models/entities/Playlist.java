package com.example.soundcloud.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "playlists_songs")
public class Playlist {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;

    private int ownerId;
    @Column
    private LocalDateTime createdAt;


    @ManyToMany
    @JoinTable(
            name = "playlists_songs",
            joinColumns = @JoinColumn(name = "playlists_id"),
            inverseJoinColumns = @JoinColumn(name = "songs_id")
    )
    private List<Song> songsInPlaylist;


}
