package com.example.soundcloud.models.services;

import com.example.soundcloud.models.dto.UserFullDTO;
import com.example.soundcloud.models.dto.song.RequestSongFilterDTO;
import com.example.soundcloud.models.dto.song.ResponseSongFilterDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.repositories.SongRepository;
import com.example.soundcloud.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {
    private SongRepository songRepository;
    private UserRepository userRepository;

    protected ModelMapper modelMapper;

    public SongService(SongRepository songRepository, UserRepository userRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public Song getById(int id){
        Song song = this.songRepository.getSongById(id);
        if(song == null) {
            throw new NotFoundException("The song with id(" + id + ") not found!");
        }
        return song;
    }

    public List<ResponseSongFilterDTO> searchByGenre(String genre){
        List<Song> songs = this.songRepository.getAllByGenre(genre);
        if(songs.size() == 0) {
            throw new NotFoundException("There is no musical content for genre: " + genre + ".");
        }
        return songs.stream().map(ResponseSongFilterDTO::new).collect(Collectors.toList());
    }

    public List<ResponseSongFilterDTO> searchByUploader(String username) {
        User uploader = this.userRepository.getUserByUsername(username);
        if(uploader == null) {
            throw new NotFoundException("There is no that user: " + username + "!");
        }

        List <Song> songs = this.songRepository.getAllByUploader(username);
        return songs.stream().map(user -> modelMapper.map(user, ResponseSongFilterDTO.class)).collect(Collectors.toList());

    }

    public User getUploader(int songId){
        Song song = this.getById(songId);
        User uploader = song.getUploader();
        return uploader;
    }

    public List<ResponseSongFilterDTO> searchLikedSongsByUser(String username) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("There is no that user with: " + username + "!");
        }
//        TODO that is ManyToMany Relationship in User`s POJO
//        List<Song> songs = user.getLikedSongsByUser();
//        return songs.stream().map(ResponseSongFilterDTO::new).collect(Collectors.toList());
        return null;
    }


    public Song searchByTitle(String title) {
        Song song = this.songRepository.getSongByTitle(title);
//        List<ResponseSongFilterDTO> songs = this.songRepository.getSongByTitle(title);
        if(song == null) {
            throw new NotFoundException("The song " + title + "not found!");
        }
        return song;
    }

    public List<ResponseSongFilterDTO> filterSongs(RequestSongFilterDTO filterType) {
//        TODO to implement logic for different filters
        return null;
    }
}
