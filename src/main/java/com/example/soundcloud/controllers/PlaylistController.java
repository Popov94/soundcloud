package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.playlist.ResponsePLDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PlaylistController extends GlobalController {

    @GetMapping("/playlist/{playlistId}")
    public ResponsePLDTO getPlaylistById(@PathVariable long playlistId) {
        System.out.println(playlistId);
        return playlistService.getPlaylistById(playlistId);
    }

    @PostMapping("/playlist")
    public ResponsePLDTO createPlayList(@RequestBody ResponsePLDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return playlistService.createPlayList(dto, userId);
    }

    //TODO validations(is logged user owner of this playlist)
    @DeleteMapping("playlist/{playlistId}")
    public String deletePlaylist(@PathVariable long playlistId, HttpServletRequest req) {
        getLoggedUserId(req);
        return playlistService.deletePlaylist(playlistId);
    }

    //TODO validations(is song already added in this list)
    @PostMapping("/playlist/{playlistId}/{songId}")
    public ResponsePLDTO addSongInPlaylist(@PathVariable long playlistId, @PathVariable long songId, HttpServletRequest req) {
        getLoggedUserId(req);
        return playlistService.addSongInPlaylist(playlistId, songId);
    }
    //TODO validations
    @PutMapping("/playlist/{playlistId}/{songId}")
    public ResponsePLDTO removeSongFromPlaylist(@PathVariable long playlistId, @PathVariable long songId, HttpServletRequest req){
        getLoggedUserId(req);
        return playlistService.removeSongFromPlayList(playlistId, songId);
    }
}
