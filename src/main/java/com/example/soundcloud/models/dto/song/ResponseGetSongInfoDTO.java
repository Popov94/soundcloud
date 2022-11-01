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

}
