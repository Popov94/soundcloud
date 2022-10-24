package com.example.soundcloud.models.dto.song;

import com.example.soundcloud.models.dto.user.DislikedUserDTO;
import com.example.soundcloud.models.dto.user.LikedUserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ResponseGetSongDTO {
    private long id;
    private String title;
    private String genre;
    private String artist;
    private String url;
    private int listened;
    private List<LikedUserDTO> likers;
    private List<DislikedUserDTO> dislikers;
    private LocalDateTime createdAt;
    private String description;
//    private  List<Comment> comments;
    //TODO how to take a list size;
    private long uploaderId;
}
