package com.example.soundcloud.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {

    private int statusCode;
    private String message;
    private LocalDateTime time;
}
