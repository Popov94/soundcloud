package com.example.soundcloud.models.dto.playlist;

import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.User;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
public class ResponsePLDTO {

    private int id;
    private String name;
    private UserWithoutPDTO owner;
    private LocalDateTime createdAt;

}
