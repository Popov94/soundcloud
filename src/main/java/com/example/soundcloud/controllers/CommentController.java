package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.comment.CommentWithoutSong;
import com.example.soundcloud.models.dto.comment.CreateCommentDTO;
import com.example.soundcloud.models.dto.comment.ResponseCommentDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CommentController extends GlobalController {

    @PostMapping("/soundcloud/{songId}/comment")
    public ResponseCommentDTO createComment(@RequestBody CreateCommentDTO dto, @PathVariable long songId, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return commentService.createComment(songId, userId, dto);
    }

    @PutMapping("/soundcloud/{songId}/{commentId}")
    public ResponseCommentDTO editComment(@RequestBody CreateCommentDTO dto, @PathVariable long songId, HttpServletRequest req, @PathVariable long commentId) {
        long userId = getLoggedUserId(req);
        return commentService.editComment(songId, userId, dto, commentId);
    }

    @DeleteMapping("/soundcloud/{songId}/{commentId}")
    public String deleteComment(@PathVariable long songId, HttpServletRequest req, @PathVariable long commentId) {
        long userId = getLoggedUserId(req);
        return commentService.deleteComment(songId, userId, commentId);
    }

    @GetMapping("/soundcloud/{songId}")
    public List<CommentWithoutSong> getSongComments(@PathVariable long songId     ) {
            return commentService.getSongComments(songId);
    }


}