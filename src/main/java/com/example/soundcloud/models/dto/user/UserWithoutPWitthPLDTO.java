package com.example.soundcloud.models.dto.user;

import com.example.soundcloud.models.dto.playlist.PlaylistWithoutOwnerDTO;
import com.example.soundcloud.models.entities.Playlist;
import lombok.Data;

import java.util.List;

@Data
public class UserWithoutPWitthPLDTO extends UserWithoutPDTO {

    private List<PlaylistWithoutOwnerDTO> playlists;
}
