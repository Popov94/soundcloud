package com.example.soundcloud.models.dto.comment;

import com.example.soundcloud.models.dto.song.SongWithoutComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutOwner {

    private long id;
    private LocalDateTime createdAt;
    private String text;
    private SongWithoutComment commentedSong;

}
