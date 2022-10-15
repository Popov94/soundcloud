package com.example.soundcloud.models.entities;


import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String firstName;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String lastName;
    @Column
    private int age;
    
    
}
