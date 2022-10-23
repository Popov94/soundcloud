package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class SongWithoutComment {

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
