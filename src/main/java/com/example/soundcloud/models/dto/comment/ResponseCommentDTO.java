package com.example.soundcloud.models.dto.comment;

import com.example.soundcloud.models.dto.song.ResponseSongDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class ResponseCommentDTO {

    private int id;
    private UserWithoutPDTO owner;
    private ResponseSongDTO commentedSong;
    private LocalDateTime createdAt;
    private String text;
}
