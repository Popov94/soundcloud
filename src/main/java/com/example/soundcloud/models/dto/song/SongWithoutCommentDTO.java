package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongWithoutCommentDTO {

    private long id;
    private String title;
    private String genre;
    private String artist;
    private int listened;
    private String url;
    private LocalDateTime createdAt;
    private String description;
    private UserWithoutPDTO uploader;
}
