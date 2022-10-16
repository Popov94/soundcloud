package com.example.soundcloud.models.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    public User(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String password;
    @Transient
    private String confirmPassword;
    @Column
    private String email;
    @Column(name = "date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate dateOfBirthday;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private LocalDateTime createdAt;
    @Column
    private String gender;
    @Column
    private LocalDateTime lastLogin;
    @Column
    private String address;
    @Column
    private String city;
    @Column
    private String country;
    @Column(name = "verified")
    private boolean isVerified;

    
    
}
