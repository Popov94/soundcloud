package com.example.soundcloud.models.dto.song;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestSongFilterDTO {
    private String title;
    private String genre;
    private LocalDate dateOfUpload;
    private int listened;
    private int likes;
    private int dislikes;
    private int comments;
    private String uploader;

}
