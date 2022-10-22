package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.entities.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class ResponseGetSongInfoDTO {

    private int id;
    private String title;
    private String genre;
    private String artist;
    private String url;
    private LocalDateTime createdAt;
    private int listened;
    private String description;
//    private List<UserDTO?> likedUsers;
//    private List<UserDTO?> dislikedUsers;
//    private List<CommentDTO?> comments;

        // TODO creating UserDTO and CommentDTO.



}
