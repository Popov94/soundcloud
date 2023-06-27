package com.example.soundcloud.models.entities.listeners;

import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users_listened_songs")
@Data
public class Listened {

    @EmbeddedId
    ListenedKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("songId")
    @JoinColumn(name = "song_id")
    Song song;
    @Column
    int listened;

}
