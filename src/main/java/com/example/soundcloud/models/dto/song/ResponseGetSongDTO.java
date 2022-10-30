package com.example.soundcloud.models.dto.song;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ResponseGetSongDTO {
    private long id;
    private String title;
    private String genre;
    private String artist;
    private String url;
    private int listened;
    private LocalDateTime createdAt;
    private String description;
    private long uploaderId;
}
