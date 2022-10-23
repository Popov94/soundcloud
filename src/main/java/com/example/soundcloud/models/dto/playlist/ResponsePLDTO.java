package com.example.soundcloud.models.dto.playlist;

import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponsePLDTO {

    private long id;
    private String name;
    private UserWithoutPDTO owner;
    private LocalDateTime createdAt;
    private List<SongWithoutUserDTO> songsInPlaylist;

}
