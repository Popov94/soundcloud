package com.example.soundcloud.models.dto.user;

import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.entities.Song;
import lombok.Data;

import java.util.List;

@Data
public class UserWithoutPWithSongsDTO extends UserWithoutPDTO{

    private List<SongWithoutUserDTO> songs;
}
