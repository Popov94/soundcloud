package com.example.soundcloud.models.dto.comment;

import com.example.soundcloud.models.dto.song.SongWithoutComment;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentedCommentDTO {

    private long id;
    private UserWithoutPDTO commentOwner;
    private LocalDateTime createdAt;
    private String text;
}
