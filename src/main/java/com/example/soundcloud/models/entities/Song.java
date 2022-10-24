package com.example.soundcloud.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "songs")
@AllArgsConstructor
public class Song {

        public Song(){

        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column
        private String title;
        @Column
        private String genre;
        @Column
        private String artist;
        @Column
        private int listened;
        @Column
        private String url;
        @Column
        private LocalDateTime createdAt;
        @Column
        private String description;
        @ManyToOne
        @JoinColumn(name = "uploader_id")
        private User uploader;
        @OneToMany(mappedBy = "commentedSong")
        private List<Comment> comments;
        @ManyToMany(mappedBy = "songsInPlaylist")
        private List<Playlist> playlist;
        @ManyToMany(mappedBy = "likedSongs")
        private List<User> likers;
        @ManyToMany(mappedBy = "dislikedSongs")
        private List<User> dislikers;
}
