package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.entities.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ResponseSongFilterDTO {
    private String title;
    private String uploader;
    private int songId;
    private int listened;
    private int likes;
    private int dislikes;
    private LocalDateTime uploadedAt;
    private int comments;

    public ResponseSongFilterDTO(Song song) {
        this.title = song.getTitle();
        this.uploader = song.getUploader().getUsername();
        this.songId = (int) song.getId();
        this.listened = song.getListened();
//        this.likes = song.getLikedUsers().size();
//        this.dislikes = song.getDislikedUsers().size();
        this.uploadedAt = song.getCreatedAt();
//        this.comments = song.getComments().size();
    }
}
