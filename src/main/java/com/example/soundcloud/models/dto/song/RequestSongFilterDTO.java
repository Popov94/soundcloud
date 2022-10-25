package com.example.soundcloud.models.dto.song;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestSongFilterDTO {
    private String title;
    private String filterBy;
    private String orderBy;
    private int page;
}
