package com.example.soundcloud.service;

import com.example.soundcloud.models.entities.Comment;
import com.example.soundcloud.models.entities.Playlist;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractService {

    @Autowired
    protected SongRepository songRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected PlaylistRepository playlistRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected Utility utility;
    @Autowired
    protected ListenedRepository listenedRepository;

    public User findUserById(long userId){
        return userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User does not exist!"));
    }

    public Song findSongById(long songId){
        return songRepository.findById(songId).orElseThrow(()-> new NotFoundException("Song does not exist!"));
    }

    public Playlist findPlaylistById(long playlistId){
        return playlistRepository.findById(playlistId).orElseThrow(() -> new NotFoundException("Playlist does not exist!"));
    }

    public Comment findCommentById(long commentId){
        return commentRepository.findById(commentId).orElseThrow(() ->new NotFoundException("Comment does not exist!"));
    }


//    public User getUserById(long userId){
////        TODO what to return full user info or not full...?
//        return userRepository.findById((long)userId).orElseThrow(() -> new NotFoundException("User does not exist!"));
//    }
}
