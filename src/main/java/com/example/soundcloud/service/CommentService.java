package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.comment.CommentWithoutSong;
import com.example.soundcloud.models.dto.comment.CreateCommentDTO;
import com.example.soundcloud.models.dto.comment.ResponseCommentDTO;
import com.example.soundcloud.models.dto.song.SongWithoutComment;
import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.UserWithoutPDTO;
import com.example.soundcloud.models.entities.Comment;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends AbstractService {

    public ResponseCommentDTO createComment(long songId, long userId, CreateCommentDTO dto) {
        User user = findUserById(userId);
        Song song = findSongById(songId);
        Comment comment = modelMapper.map(dto, Comment.class);
        comment.setCommentedSong(song);
        comment.setCommentOwner(user);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        ResponseCommentDTO respDTO = modelMapper.map(comment, ResponseCommentDTO.class);
        respDTO.setCommentedSong(modelMapper.map(comment.getCommentedSong(), SongWithoutComment.class));
        respDTO.setCommentOwner(modelMapper.map(comment.getCommentOwner(), UserWithoutPDTO.class));
        respDTO.getCommentedSong().setUploader(modelMapper.map(respDTO.getCommentedSong().getUploader(), UserWithoutPDTO.class));
        return respDTO;

    }

    public ResponseCommentDTO editComment(long songId, long userId, CreateCommentDTO dto, long commentId) {
        User user = findUserById(userId);
        Song song = findSongById(songId);
        Comment comment = findCommentById(commentId);
        if (comment.getCommentOwner().getId() != user.getId()) {
            throw new BadRequestException("You can edit only your comments!");
        }
        if (!dto.getText().isBlank()) {
            if (!(dto.getText().length() > 500)) {
                comment.setText(dto.getText());
            } else {
                throw new BadRequestException("Text can not be more then 1000 symbols!");
            }
        } else {
            throw new BadRequestException("You can not leave empty comment. You can delete it instead!");
        }
        commentRepository.save(comment);
        ResponseCommentDTO respDTO = modelMapper.map(comment, ResponseCommentDTO.class);
        respDTO.setCommentedSong(modelMapper.map(comment.getCommentedSong(), SongWithoutComment.class));
        respDTO.setCommentOwner(modelMapper.map(comment.getCommentOwner(), UserWithoutPDTO.class));
        respDTO.getCommentedSong().setUploader(modelMapper.map(respDTO.getCommentedSong().getUploader(), UserWithoutPDTO.class));
        return respDTO;
    }

    public String deleteComment(long songId, long userId, long commentId) {
        Song song = findSongById(songId);
        User user = findUserById(userId);
        Comment comment = findCommentById(commentId);
        if (user.getId() == comment.getCommentOwner().getId()){
            commentRepository.delete(comment);
            return "Comment with " + comment.getId() + " was deleted successfully";
        }else {
            throw new BadRequestException("Only owner of the comment can delete it");
        }
    }

    public List<CommentWithoutSong> getSongComments(long songId) {
        Song song = findSongById(songId);
        List<Comment> comments = commentRepository.findCommentBySong(songId);
        List<CommentWithoutSong> commentsDTO = comments.stream().map(comment -> modelMapper.map(comment, CommentWithoutSong.class)).collect(Collectors.toList());
        for (CommentWithoutSong com : commentsDTO){
            com.setCommentOwner(modelMapper.map(com.getCommentOwner(), UserWithoutPDTO.class));
        }
        return commentsDTO;
    }

    //TODO fix bug bcs one time shows im verified and one time not
}
