package com.example.soundcloud.models.dto.song;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongWithoutUserDTO {

    private long id;
    private String title;
    private String genre;
    String artist;
    private int listened;
    private String url;
    private LocalDateTime createdAt;
    private String description;

}
