package com.example.soundcloud.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String text;
    @Column
    private LocalDateTime createdAt;
    @Column
    private int owner;

    @ManyToOne
    @JoinColumn(name = "comments")
    private Song song;




}
