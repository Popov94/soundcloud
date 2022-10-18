package com.example.soundcloud.models.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EditDTO {

    private String username;
    private String email;
    private LocalDate dateOfBirthday;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String city;
    private String country;
    private String currentPassword;
}
