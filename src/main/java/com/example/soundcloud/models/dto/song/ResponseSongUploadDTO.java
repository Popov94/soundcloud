package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.entities.Song;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class ResponseSongUploadDTO {
    private long id;
    private String title;
    private String artist;
    private String genre;
    private String url;
    private LocalDateTime createdAt;
    private long uploaderId;
}
