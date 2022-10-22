package com.example.soundcloud.models.dto.song;

import lombok.Data;

@Data
public class RequestSongEditDTO {
    private String title;
    private String artist;
    private String genre;
    private String description;
}
