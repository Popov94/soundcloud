package com.example.soundcloud.models.entities;

import com.example.soundcloud.models.dto.comment.CommentedCommentDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    public Comment(){

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentOwner;
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song commentedSong;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Column
    private String text;
    @ManyToMany(mappedBy = "likedComments")
    private List<User> commentLikers;
    @ManyToMany(mappedBy = "dislikedComments")
    private List<User> commentDislikers;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment commentedComment;


}
