package com.example.soundcloud.models.dto.song;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseGetSongByUsernameDTO {
    private String title;
    private long songId;
    private int listened;
    private int likes;
    private int dislikes;
    private LocalDateTime createdAt;
//    private int comments;
}