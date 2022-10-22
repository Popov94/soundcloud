package com.example.soundcloud.models.dto.playlist;

import com.example.soundcloud.models.entities.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaylistWithoutOwnerDTO {

    private int id;
    private String name;
    private LocalDateTime createdAt;

}
