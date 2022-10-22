package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.song.ResponseGetSongDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.repositories.SongRepository;
import com.example.soundcloud.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractService {

    @Autowired
    protected SongRepository songRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ModelMapper modelMapper;

    public User findUserById(long userId){
        return userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User does not exist!"));
    }

    public Song findSongById(long songId){
        return  songRepository.findById(songId).orElseThrow(()-> new NotFoundException("Song does not exist!"));
    }

}
