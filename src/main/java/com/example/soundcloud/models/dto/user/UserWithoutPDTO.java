package com.example.soundcloud.models.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
public class UserWithoutPDTO {

    private long id;
    private String username;
    private String email;
    private String dateOfBirthday;
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
    private String profileImageUrl;
    private boolean isVerified;


}
