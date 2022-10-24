package com.example.soundcloud.models.dto.song;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseSongDeleteDTO {
    private String message;
    private long deletedSongId;
}
