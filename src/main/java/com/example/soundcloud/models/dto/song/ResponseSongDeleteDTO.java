package com.example.soundcloud.models.dto.song;

import lombok.Data;

@Data
public class ResponseSongDeleteDTO {
    private String message;
    private long deletedSongId;
}
