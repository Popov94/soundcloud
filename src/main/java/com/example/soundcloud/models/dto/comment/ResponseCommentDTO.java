package com.example.soundcloud.models.dto.comment;

import com.example.soundcloud.models.dto.song.ResponseSongDTO;
import com.example.soundcloud.models.dto.song.SongWithoutCommentDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseCommentDTO {

    private long id;
    private UserWithoutPDTO commentOwner;
    private SongWithoutCommentDTO commentedSong;
    private LocalDateTime createdAt;
    private String text;

}
