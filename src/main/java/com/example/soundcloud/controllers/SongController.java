package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.song.*;
import com.example.soundcloud.models.dto.user.EditDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController extends GlobalController {
    @Autowired
    private SongService songService;

    @GetMapping("/{sid}")
    public ResponseSongDTO getSongWithUserById(@PathVariable long sid) {
        System.out.println(sid);
        return songService.getSongWithUserById(sid);
    }

    @GetMapping("/{sid}/info")
    public ResponseGetSongInfoDTO searchById(@PathVariable long sid) {
        Song song = songService.findSongById(sid);
        return modelMapper.map(song, ResponseGetSongInfoDTO.class);
    }

    @GetMapping("/by_username/{username}")
    public List<ResponseGetSongByUsernameDTO> searchByUploader(@PathVariable String username) {
        return this.songService.searchByUploader(username);
    }

    @GetMapping("/{username}/liked")
    public List<ResponseGetSongDTO> searchLikedSongs(@PathVariable String username) {
        return this.songService.searchLikedSongsByUser(username);
    }

    @GetMapping("/by_genre/{genre}")
    public List<ResponseGetSongDTO> searchByGenre(@PathVariable String genre) {
        return this.songService.searchByGenre(genre);
    }

    @PostMapping("/filter")
    public List<ResponseSongFilterDTO> filterSongs(@RequestBody RequestSongFilterDTO filterType) throws SQLException {
        return this.songService.filterSongs(filterType);
    }

    //TODO to improve the method - return a List with songs where the titles contains a given String;
    @GetMapping("/by_title/{title}")
    public ResponseGetSongDTO searchByTitle(@PathVariable String title) {
        return this.songService.searchByTitle(title);
    }

    @PostMapping("/{sid}/like")
    public LikeDTO like(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        this.songService.isSongDisliked(sid, uid);
        return songService.like(sid, uid);
    }

    @PostMapping("/{sid}/dislike")
    public DislikeDTO dislike(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        this.songService.isSongLiked(sid, uid);
        return songService.dislike(sid, uid);
    }

    @PostMapping("/upload")
    public ResponseSongUploadDTO upload(@RequestParam(value = "file") MultipartFile songFile, @RequestParam String title, @RequestParam String artist, @RequestParam String genre, @RequestParam String description, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.uploadSong(uid, title, artist, genre, description, songFile);
    }

    @DeleteMapping("/{sid}")
    public ResponseSongDeleteDTO delete(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.deleteSong(uid, sid);
    }

    @PutMapping("/{sid}")
    public ResponseGetSongInfoDTO editInfo(@PathVariable long sid, @RequestBody RequestSongEditDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return songService.editSong(dto, userId, sid);
    }
//TODO play song

}
