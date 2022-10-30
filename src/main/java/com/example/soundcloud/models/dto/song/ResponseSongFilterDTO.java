package com.example.soundcloud.models.dto.song;

import lombok.*;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class ResponseSongFilterDTO {
    private String title;
    private String uploader;
    private long songId;
    private int listened;
    private int likes;
    private int dislikes;
    private LocalDateTime uploadedAt;
    private int comments;

}
