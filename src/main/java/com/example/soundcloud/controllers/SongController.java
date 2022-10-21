package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.song.*;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController extends GlobalController {
    @Autowired
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

    @GetMapping("/{sid}")
    public ResponseSongDTO getSongWithUserById(@PathVariable long sid){
        System.out.println(sid);
        return songService.getSongWithUserById(sid);
    }

    @GetMapping("/{sid}/info")
    public ResponseGetSongInfoDTO searchById(@PathVariable long sid){
        return new ResponseGetSongInfoDTO(songService.findSongById(sid));
    }

    @GetMapping("/by_username/{username}")
    public List<ResponseSongFilterDTO> searchByUploader(@PathVariable String username){
        return this.songService.searchByUploader(username);
    }

    @GetMapping("/{username}/liked")
    public List<ResponseSongFilterDTO> searchLikedSongs(@PathVariable String username){
        return this.songService.searchLikedSongsByUser(username);
    }

    @GetMapping("/by_genre/{genre}")
    public List<ResponseSongFilterDTO> searchByGenre(@PathVariable String genre){
        return this.songService.searchByGenre(genre);
    }

    @GetMapping("by_filter/{filterType}")
    public List<ResponseSongFilterDTO> filterSongs(@RequestBody RequestSongFilterDTO filterType){
        return this.songService.filterSongs(filterType);
    }


    //TODO to improve the method - return a List with songs where the titles contains a given String;
    @GetMapping("/by_title")
    public ResponseSongFilterDTO searchByTitle(String title){
        return this.songService.searchByTitle(title);
    }


    //TODO like/dislike song
    @PostMapping("/{sid}/like")
    public LikeDTO like(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.like(sid, uid);
    }

    @PostMapping("/{sid}/dislike")
    public DislikeDTO dislike(@PathVariable long sid, HttpServletRequest request){
        long uid = getLoggedUserId(request);
        return songService.dislike(sid,uid);
    }



    //TODO edit info;
    //TODO upload a song;
    //TODO Delete a song;

}
