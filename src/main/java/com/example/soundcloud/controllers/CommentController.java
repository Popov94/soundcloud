package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.comment.ResponseCommentDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController extends GlobalController{

//    @PostMapping("/soundcloud/{songId}/comment")
//    public ResponseCommentDTO createComment(@PathVariable long songId, HttpServletRequest req,){
//
//    }
}
