package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.song.*;
import com.example.soundcloud.models.dto.user.APIResponse;
import com.example.soundcloud.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class SongController extends GlobalController {
    @Autowired
    private SongService songService;

    @GetMapping("/songs/{sid}")
    public ResponseSongDTO getSongWithUserById(@PathVariable long sid) {
        return songService.getSongWithUserById(sid);
    }

    @GetMapping("/songs/{sid}/info")
    public ResponseGetSongInfoDTO searchById(@PathVariable long sid) {
        return songService.getSongInfo(sid);
    }

    @GetMapping("/songs/by_user_id/{uid}")
    public List<ResponseGetSongInfoDTO> searchByUserId(@PathVariable long uid) {
        return this.songService.searchByUploader(uid);
    }

    @GetMapping("/songs/{uid}/liked")
    public List<ResponseGetSongDTO> searchLikedSongs(@PathVariable long uid) {
        return this.songService.searchLikedSongsByUser(uid);
    }

    @GetMapping("/songs/by_genre/{genre}")
    public List<ResponseGetSongDTO> searchByGenre(@PathVariable String genre) {
        return this.songService.searchByGenre(genre);
    }

    @PostMapping("/songs/filter")
    public List<ResponseSongFilterDTO> filterSongs(@RequestBody RequestSongFilterDTO filterType) throws SQLException {
        return this.songService.filterSongs(filterType);
    }

    @GetMapping("/songs/by_title/{title}")
    public List<ResponseGetSongDTO> searchByTitle(@PathVariable String title) {
        return this.songService.searchByTitle(title);
    }

    @PostMapping("/songs/{sid}/like")
    public LikeDTO like(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.like(sid, uid);
    }

    @PostMapping("/songs/{sid}/dislike")
    public DislikeDTO dislike(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.dislike(sid, uid);
    }

    @PostMapping("/songs/upload")
    public ResponseSongUploadDTO upload(@RequestParam(value = "file") MultipartFile songFile, @RequestParam String title,
                                        @RequestParam String artist, @RequestParam String genre,
                                        @RequestParam String description, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.uploadSong(uid, title, artist, genre, description, songFile);
    }

    @DeleteMapping("/songs/{sid}")
    public ResponseSongDeleteDTO delete(@PathVariable long sid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return songService.deleteSong(uid, sid);
    }

    @PutMapping("/songs/{sid}")
    public ResponseGetSongInfoDTO editInfo(@PathVariable long sid, @RequestBody RequestSongEditDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return songService.editSong(dto, userId, sid);
    }

    @PutMapping("/songs/{sid}/play")
    public void playSong(@PathVariable long sid, HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        long userId = 0;
        if (session.getAttribute(LOGGED)!= null){
            userId = (long) session.getAttribute(USER_ID);
        }
        songService.play(sid, userId, response);
    }

    @GetMapping("/songs/feed/top_genre_for_user/{page}")
    public List<ResponseSongFilterDTO> topGenreSongsForUser(@PathVariable int page, HttpServletRequest req) throws SQLException {
        long uid = getLoggedUserId(req);
        return songService.topGenreSongsForUser(uid, page);
    }

    @GetMapping("/songs/feed/top_genre/{page}")
    public List<ResponseSongFilterDTO> topGenreSongs(@PathVariable int page) throws SQLException {
        return songService.topGenreSongs(page);
    }

    @GetMapping("/songs/feed/top_listened/{page}")
    public List<ResponseSongFilterDTO> topTenListened(@PathVariable int page) throws SQLException {
        return songService.topListened( page);
    }

    @GetMapping("/songs/search/{offset}/{pageSize}/{sortedBy}")
    public APIResponse<Page<ResponseSongDTO>> sortSongWithPagination(@PathVariable int offset, @PathVariable int pageSize,
                                                                     @PathVariable String sortedBy) {
        Page<ResponseSongDTO> pageDTO = songService.sortSongWithPagination(offset, pageSize, sortedBy);
        return new APIResponse<>(pageDTO.getSize(), pageDTO);
    }

    @GetMapping("/songs/search/{keyword}/{offset}/{pageSize}/{sortedBy}/{kindOfSort}")
    public APIResponse<Page<ResponseSongDTO>> searchSong(@PathVariable String keyword, @PathVariable int offset,
                                                         @PathVariable int pageSize, @PathVariable String sortedBy,
                                                         @PathVariable String kindOfSort) {
    Page<ResponseSongDTO> page = songService.searchSong(keyword,offset,pageSize,sortedBy,kindOfSort);
    return new APIResponse<>(page.getSize(), page);
    }
}
