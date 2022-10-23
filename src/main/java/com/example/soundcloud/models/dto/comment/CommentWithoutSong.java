package com.example.soundcloud.models.dto.comment;

import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutSong {
    private long id;
    private LocalDateTime createdAt;
    private String text;
    private UserWithoutPDTO commentOwner;
}
