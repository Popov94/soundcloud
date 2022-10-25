package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.dto.comment.CommentWithoutSong;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseSongDTO {
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
