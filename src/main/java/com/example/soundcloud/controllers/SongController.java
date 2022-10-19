package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.song.ResponseGetSongInfoDTO;
import com.example.soundcloud.models.dto.song.RequestSongFilterDTO;
import com.example.soundcloud.models.dto.song.ResponseSongFilterDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.repositories.SongRepository;
import com.example.soundcloud.models.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {
    @Autowired
    private SongRepository songRepository;
    private SongService songService;

//    GET:
//    - Search for songs (by name, by genre, by uploader etc.);
//    - Filter songs (by likes, by dates etc.);
//    - Info.
//
//    POST:
//    - Upload song.
//
//    PUT:
//    - Like song;
//    - Dislike song;
//    - Edit.
//
//    DELETE:
//    - Delete.

    @GetMapping("/songs/{id}/info")
    public ResponseGetSongInfoDTO searchById(@PathVariable int id){
        return new ResponseGetSongInfoDTO(this.songService.getById(id));
    }

    @GetMapping("/songs/by_username/{username}")
    public List<ResponseSongFilterDTO> searchByUploader(@PathVariable String username){
        return this.songService.searchByUploader(username);
    }

    @GetMapping("/songs/{username}/liked")
    public List<ResponseSongFilterDTO> searchLikedSongs(@PathVariable String username){
        return this.songService.searchLikedSongsByUser(username);
    }

    @GetMapping("/songs/by_genre/{genre}")
    public List<ResponseSongFilterDTO> searchByGenre(@PathVariable String genre){
        return this.songService.searchByGenre(genre);
    }


    //TODO filter songs (by likes, by dates etc.);
    @GetMapping
    public List<ResponseSongFilterDTO> filterSongs(@RequestBody RequestSongFilterDTO filterType){
        return this.songService.filterSongs(filterType);
    }



//    TODO to return a List with songs where the titles contains a given String;
    @GetMapping("/songs/by_title")
    public ResponseSongFilterDTO searchByTitle(String title){
        return new ResponseSongFilterDTO(this.songService.searchByTitle(title));
    }



    //TODO upload
//
//    public Song upload() {
//    }

    //TODO like/dislike song
    //TODO edit info;
    //TODO Delete song;


}
