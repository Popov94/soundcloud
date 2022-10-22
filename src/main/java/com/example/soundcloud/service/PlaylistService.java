package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.playlist.ResponsePLDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Playlist;
import com.example.soundcloud.models.entities.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlaylistService extends AbstractService{


    public ResponsePLDTO getPlaylistById(long playlistId) {
        Playlist playlist = findPlaylistById(playlistId);
        ResponsePLDTO dto = modelMapper.map(playlist,ResponsePLDTO.class);
        dto.setOwner(modelMapper.map(dto.getOwner(), UserWithoutPDTO.class));
        return dto;

    }

    public ResponsePLDTO createPlayList(ResponsePLDTO dto, long userId) {
        User user = findUserById(userId);
        Playlist playlist = modelMapper.map(dto, Playlist.class);
        playlist.setOwner(user);
        playlist.setCreatedAt(LocalDateTime.now());
        playlistRepository.save(playlist);
        ResponsePLDTO respDTO = modelMapper.map(playlist,ResponsePLDTO.class);
        respDTO.setOwner(modelMapper.map(respDTO.getOwner(),UserWithoutPDTO.class));
        return respDTO;
    }
}
