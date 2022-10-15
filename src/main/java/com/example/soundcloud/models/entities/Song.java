package com.example.soundcloud.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "songs")
public class Song {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column
        private String title;
        @Column
        private int views;
        @Column
        private LocalDate createdAt;
        @Column
        private String url;
        @Column
        private int uploaderId;

    }
