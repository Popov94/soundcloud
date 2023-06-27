package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.comment.CommentWithoutSong;
import com.example.soundcloud.models.dto.comment.CommentedCommentDTO;
import com.example.soundcloud.models.dto.comment.CreateCommentDTO;
import com.example.soundcloud.models.dto.comment.ResponseCommentDTO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CommentController extends GlobalController {

    @PostMapping("/soundcloud/{songId}/comment")
    public ResponseCommentDTO createComment(@RequestBody CreateCommentDTO dto, @PathVariable long sid, HttpServletRequest req) {
        long uid = getLoggedUserId(req);
        return commentService.createComment(sid, uid, dto);
    }

    @PostMapping("/soundcloud/comment/{commentId}")
    public CommentedCommentDTO commentComment(@RequestBody CreateCommentDTO dto, @PathVariable long cid, HttpServletRequest req) {
        long uid = getLoggedUserId(req);
        return commentService.commentComment(uid, dto, cid);
    }

    @PutMapping("/soundcloud/{songId}/{commentId}")
    public ResponseCommentDTO editComment(@RequestBody CreateCommentDTO dto, @PathVariable long sid, HttpServletRequest req, @PathVariable long cid) {
        long uid = getLoggedUserId(req);
        return commentService.editComment(sid, uid, dto, cid);
    }
    //TODO song e izlishno

    @DeleteMapping("/soundcloud/{songId}/{commentId}")
    public String deleteComment(@PathVariable long sid, HttpServletRequest req, @PathVariable long cid) {
        long uid = getLoggedUserId(req);
        return commentService.deleteComment(sid, uid, cid);
    }

    @GetMapping("/soundcloud/{songId}")
    public List<CommentWithoutSong> getSongComments(@PathVariable long sid) {
        return commentService.getSongComments(sid);
    }

    @PostMapping("/soundcloud/{cid}/like")
    public LikeDTO like(@PathVariable long cid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return commentService.like(cid, uid);
    }

    //TODO like/dislike - tranzakcii?

    @PostMapping("/soundcloud/{cid}/dislike")
    public DislikeDTO dislike(@PathVariable long cid, HttpServletRequest request) {
        long uid = getLoggedUserId(request);
        return commentService.dislike(cid, uid);
    }
}