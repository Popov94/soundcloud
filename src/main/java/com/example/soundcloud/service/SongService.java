package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.song.RequestSongFilterDTO;
import com.example.soundcloud.models.dto.song.ResponseSongDTO;
import com.example.soundcloud.models.dto.song.ResponseSongFilterDTO;
import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService extends AbstractService {

    public List<ResponseSongFilterDTO> searchByGenre(String genre) {
        List<Song> songs = this.songRepository.getAllByGenre(genre);
        if (songs.size() == 0) {
            throw new NotFoundException("There is no musical content for genre: " + genre + ".");
        }
        return songs.stream().map(song -> modelMapper.map(song, ResponseSongFilterDTO.class)).collect(Collectors.toList());
    }

    public List<ResponseSongFilterDTO> searchByUploader(String username) {

        User uploader = this.userRepository.getUserByUsername(username);
        if (uploader == null) {
            throw new NotFoundException("There is no that user: " + username + "!");
        }

        List<Song> songs = this.songRepository.getAllByUploader(username);
        if (songs.size() == 0) {
            throw new NotFoundException("This user did not upload any musical content!");
        }
        return songs.stream().map(song -> modelMapper.map(song, ResponseSongFilterDTO.class)).collect(Collectors.toList());
    }

    public List<ResponseSongFilterDTO> searchLikedSongsByUser(String username) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("There is no user with username: " + username + "!");
        }
//        TODO that is ManyToMany Relationship in User`s POJO
//        List<Song> songs = user.getLikedSongsByUser();
//        if(songs.size() == 0) {
//            throw new NotFoundException("This user has not liked any songs!");
//        }
//        return songs.stream().map(song -> modelMapper.map(song, ResponseSongFilterDTO.class)).collect(Collectors.toList());
        return null;
    }

    public ResponseSongFilterDTO searchByTitle(String title) {
        Song song = this.songRepository.getSongByTitle(title);
        if (song == null) {
            throw new NotFoundException("The song " + title + " not found!");
        }
        return new ResponseSongFilterDTO(song);
    }

    public List<ResponseSongFilterDTO> filterSongs(RequestSongFilterDTO filterType) {

        String title = filterType.getTitle();
        if (title == null) {
            throw new BadRequestException("You can not search without a title!");
        }

        String filterBy = filterType.getFilterBy();
        if (filterBy == null) {
            filterBy = "likes";
        } else {
            filterBy = filterBy.toLowerCase().trim();
        }

        String orderBy = filterType.getOrderBy();
        if (orderBy == null) {
            orderBy = "asc";
        } else {
            orderBy = orderBy.toLowerCase().trim();
            if (!orderBy.equals("asc") && !orderBy.equals("desc")) {
                throw new BadRequestException("Invalid type of ordering!");
            }
        }

        Integer page = filterType.getPage();
        if (page == null) {
            page = 1;
        }

        switch (filterBy) {
            case "likes":
            case "dislikes":
            case "date":
            case "listened":
            case "comments":
                return null;
//        TODO to implement an SQL query to return a song from DB
            default:
                throw new BadRequestException("Invalid type of filter!");
        }
    }

    public LikeDTO like(long songId, long userId) {
        Song song = findSongById(songId);
        User user = findUserById(userId);
        if (user.getLikedSongs().contains(song)) {
            user.getLikedSongs().remove(song);
        } else {
            user.getLikedSongs().add(song);
        }
        userRepository.save(user);
        return new LikeDTO("Your like was successfully accepted!", song.getLikers().size());
    }

    public DislikeDTO dislike(long songId, long userId) {
        Song song = findSongById(songId);
        User user = findUserById(userId);
        if (user.getDislikedSongs().contains(song)) {
            user.getDislikedSongs().remove(song);
        } else {
            user.getDislikedSongs().add(song);
        }
        userRepository.save(user);
        return new DislikeDTO("Your dislike was successfully accepted!", song.getDislikers().size());
    }

    public ResponseSongDTO getSongWithUserById(long songId) {
        System.out.println(songId);
        Song song = findSongById(songId);
        ResponseSongDTO dto = modelMapper.map(song,ResponseSongDTO.class);
        dto.setUploader(modelMapper.map(dto.getUploader(), UserWithoutPDTO.class));
        return dto;


    }

}
