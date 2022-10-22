package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.playlist.ResponsePLDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PlaylistController extends GlobalController{

    @GetMapping("/playlist/{playlistId}")
    public ResponsePLDTO getPlaylistById(@PathVariable long playlistId){
        System.out.println(playlistId);
        return playlistService.getPlaylistById(playlistId);
    }
    @PostMapping("/playlist")
    public ResponsePLDTO createPlayList(@RequestBody ResponsePLDTO dto, HttpServletRequest req){
        long userId = getLoggedUserId(req);
        return playlistService.createPlayList(dto,userId);
    }
}
