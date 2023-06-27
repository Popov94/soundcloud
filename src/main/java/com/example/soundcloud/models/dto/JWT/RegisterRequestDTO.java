package com.example.soundcloud.models.dto.JWT;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
//    private String username;
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String password;


    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private LocalDate dateOfBirthday;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;
    private String address;
    private String city;
    private String country;
}
