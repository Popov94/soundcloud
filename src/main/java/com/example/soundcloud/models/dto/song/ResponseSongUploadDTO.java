package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.entities.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseSongUploadDTO {
    private int id;
    private String title;
    private String artist;
    private String genre;
    private String url;
    private LocalDateTime createdAt;


    public ResponseSongUploadDTO(Song song){
        this.id = song.getId();
        this.title = song.getTitle();
        this.genre = song.getGenre();
        this.artist = song.getArtist();
        this.url = song.getUrl();
        this.createdAt = song.getCreatedAt();
    }
}
