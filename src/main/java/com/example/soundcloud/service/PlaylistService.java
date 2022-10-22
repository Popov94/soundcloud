package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.playlist.ResponsePLDTO;
import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Playlist;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class PlaylistService extends AbstractService {


    public ResponsePLDTO getPlaylistById(long playlistId) {
        Playlist playlist = findPlaylistById(playlistId);
        ResponsePLDTO dto = modelMapper.map(playlist, ResponsePLDTO.class);
        dto.setOwner(modelMapper.map(dto.getOwner(), UserWithoutPDTO.class));
        return dto;

    }

    public ResponsePLDTO createPlayList(ResponsePLDTO dto, long userId) {
        if (utility.PlaylistNameValidation(dto.getName()) && !utility.isPlaylistExist(dto.getName())) {
            User user = findUserById(userId);
            Playlist playlist = modelMapper.map(dto, Playlist.class);
            playlist.setOwner(user);
            playlist.setCreatedAt(LocalDateTime.now());
            playlistRepository.save(playlist);
            ResponsePLDTO respDTO = modelMapper.map(playlist, ResponsePLDTO.class);
            respDTO.setOwner(modelMapper.map(respDTO.getOwner(), UserWithoutPDTO.class));
            return respDTO;
        } else {
            throw new BadRequestException("Title is invalid!");
        }
    }

    public ResponsePLDTO addSongInPlaylist(long playlistId, long songId) {
        Playlist playlist = findPlaylistById(playlistId);
        Song song = findSongById(songId);
        playlist.getSongsInPlaylist().add(song);
        playlistRepository.save(playlist);
        ResponsePLDTO dto = modelMapper.map(playlist,ResponsePLDTO.class);
        dto.setSongsInPlaylist(playlist.getSongsInPlaylist().stream().map(song1 -> modelMapper.map(song1,SongWithoutUserDTO.class)).collect(Collectors.toList()));
        return dto;
    }

    public String deletePlaylist(long playlistId) {
        Playlist playlist = findPlaylistById(playlistId);
        playlistRepository.delete(playlist);
        return playlist.getName() + " was successfully deleted!";
    }

    public ResponsePLDTO removeSongFromPlayList(long playlistId, long songId) {
        Playlist playlist = findPlaylistById(playlistId);
        Song song = findSongById(songId);
        playlist.getSongsInPlaylist().remove(song);
        playlistRepository.save(playlist);
        ResponsePLDTO dto = modelMapper.map(playlist,ResponsePLDTO.class);
        dto.setSongsInPlaylist(playlist.getSongsInPlaylist().stream().map(song1 -> modelMapper.map(song1,SongWithoutUserDTO.class)).collect(Collectors.toList()));
        return dto;
    }
}
