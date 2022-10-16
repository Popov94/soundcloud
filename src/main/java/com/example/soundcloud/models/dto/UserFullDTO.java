package com.example.soundcloud.models.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UserFullDTO {

    private long id;
    private String username;
    private String email;
    private String dateOfBirthday;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private String gender;
    private LocalDateTime lastLogin;
    private String address;
    private String city;
    private String country;
    private boolean isVerified;


}
