package com.example.soundcloud.models.dto.song;

import lombok.Data;

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
