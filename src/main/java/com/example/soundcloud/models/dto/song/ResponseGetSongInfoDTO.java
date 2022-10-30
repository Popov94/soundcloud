package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.dto.user.UserInfoDTO;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetSongInfoDTO {
    private long id;
    private String title;
    private String genre;
    private String artist;
    private String url;
    private LocalDateTime createdAt;
    private int listened;
    private String description;
    private UserInfoDTO uploader;
    private int likes;
    private int dislikes;
    private int comments;

//    public ResponseGetSongInfoDTO (long id, String title, String genre, String artist, String url,
//                                   String description, LocalDateTime createdAt, int listened, int likes,
//                                   UserInfoDTO uploader, int dislikes, int comments, UserInfoDTO user){
//        ResponseGetSongInfoDTO copyDTO = new ResponseGetSongInfoDTO();
//        copyDTO.setId(id);
//        copyDTO.setTitle(title);
//        copyDTO.setGenre(genre);
//        copyDTO.setArtist(artist);
//        copyDTO.setUrl(url);
//        copyDTO.setDescription(description);
//        copyDTO.setCreatedAt(createdAt);
//        copyDTO.setListened(listened);
//        copyDTO.setLikes(likes);
//        copyDTO.setUploader(uploader);
//        copyDTO.setDislikes(dislikes);
//        copyDTO.setComments(comments);
//        copyDTO.setUploader(user);
//    }

//    public ResponseGetSongInfoDTO (Song songToCopy){
//        ResponseGetSongInfoDTO copyDTO = new ResponseGetSongInfoDTO();
//        copyDTO.setId(songToCopy.getId());
//        copyDTO.setTitle(songToCopy.getTitle());
//        copyDTO.setGenre(songToCopy.getGenre());
//        copyDTO.setArtist(songToCopy.getGenre());
//        copyDTO.setUrl(songToCopy.getUrl());
//        copyDTO.setDescription(songToCopy.getDescription());
//        copyDTO.setCreatedAt(songToCopy.getCreatedAt());
//        copyDTO.setListened(songToCopy.getListened());
//        copyDTO.setLikes(songToCopy.getLikers().size());
//        copyDTO.setDislikes(songToCopy.getDislikers().size());
//        copyDTO.setComments(songToCopy.getComments().size());
//    }
}
