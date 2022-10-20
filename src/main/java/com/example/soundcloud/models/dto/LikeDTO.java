package com.example.soundcloud.models.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    private String message;
    private int updatedLikes;

}
