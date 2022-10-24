package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.DislikeDTO;
import com.example.soundcloud.models.dto.LikeDTO;
import com.example.soundcloud.models.dto.comment.CommentWithoutSong;
import com.example.soundcloud.models.dto.comment.CommentedCommentDTO;
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
        if (utility.isTextValid(dto)) {
            Comment comment = modelMapper.map(dto, Comment.class);
            comment.setCommentedSong(song);
            comment.setCommentOwner(user);
            comment.setCreatedAt(LocalDateTime.now());
            commentRepository.save(comment);
            return getResponseCommentDTO(comment);
        } else {
            throw new BadRequestException("Invalid text");
        }

    }

    private ResponseCommentDTO getResponseCommentDTO(Comment comment) {
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
        if (utility.isTextValid(dto)) {
            comment.setText(dto.getText());
            commentRepository.save(comment);
            return getResponseCommentDTO(comment);
        } else {
            throw new BadRequestException("Text is invalid");
        }

    }

    public String deleteComment(long songId, long userId, long commentId) {
        Song song = findSongById(songId);
        User user = findUserById(userId);
        Comment comment = findCommentById(commentId);
        if (user.getId() == comment.getCommentOwner().getId()) {
            commentRepository.delete(comment);
            return "Comment with " + comment.getId() + " was deleted successfully";
        } else {
            throw new BadRequestException("Only owner of the comment can delete it");
        }
    }

    public List<CommentWithoutSong> getSongComments(long songId) {
        Song song = findSongById(songId);
        List<Comment> comments = commentRepository.findCommentBySong(songId);
        List<CommentWithoutSong> commentsDTO = comments.stream()
                .map(comment -> modelMapper.map(comment, CommentWithoutSong.class))
                .collect(Collectors.toList());
        for (CommentWithoutSong com : commentsDTO) {
            com.setCommentOwner(modelMapper.map(com.getCommentOwner(), UserWithoutPDTO.class));
        }
        return commentsDTO;
    }


    public LikeDTO like(long cid, long uid) {
        Comment comment = commentRepository.findCommentById(cid);
        User user = findUserById(uid);
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
            userRepository.save(user);
            return new LikeDTO("Your like was successfully removed!", comment.getCommentLikers().size());
        } else {
            user.getLikedComments().add(comment);
            userRepository.save(user);
            return new LikeDTO("Your like was successfully accepted!", comment.getCommentLikers().size());
        }
    }

    public DislikeDTO dislike(long cid, long uid) {
        Comment comment = commentRepository.findCommentById(cid);
        User user = findUserById(uid);
        if (user.getDislikedComments().contains(comment)) {
            user.getDislikedComments().remove(comment);
            userRepository.save(user);
            return new DislikeDTO("Your dislike was successfully removed!", comment.getCommentDislikers().size());
        } else {
            user.getDislikedComments().add(comment);
            userRepository.save(user);
            return new DislikeDTO("Your dislike was successfully accepted!", comment.getCommentDislikers().size());
        }
    }

    public void isCommentDisliked(long cid, long uid) {
        Comment comment = commentRepository.findCommentById(cid);
        User currentUser = modelMapper.map(userRepository.findById(uid), User.class);
        if (comment.getCommentDislikers().contains(currentUser)) {
            currentUser.getDislikedComments().remove(comment);
        }
    }

    public void isCommentLiked(long cid, long uid) {
        Comment comment = commentRepository.findCommentById(cid);
        User currentUser = modelMapper.map(userRepository.findById(uid), User.class);
        if (comment.getCommentLikers().contains(currentUser)) {
            currentUser.getLikedComments().remove(comment);
        }
    }

    public CommentedCommentDTO commentComment(long songId, long userId, CreateCommentDTO dto, long commentId) {
        if (utility.isTextValid(dto)) {
            User user = findUserById(userId);
            Song song = findSongById(songId);
            Comment parentC = findCommentById(commentId);
            Comment childC = new Comment();
            childC.setText(dto.getText());
            childC.setCommentedSong(song);
            childC.setCreatedAt(LocalDateTime.now());
            childC.setCommentOwner(user);
            childC.setCommentedComment(parentC);
            commentRepository.save(childC);
            CommentedCommentDTO dtoCC = modelMapper.map(childC, CommentedCommentDTO.class);
            dtoCC.setCommentOwner(modelMapper.map(dtoCC.getCommentOwner(), UserWithoutPDTO.class));
            return dtoCC;
        } else {
            throw new BadRequestException("Invalid text!");
        }
    }
}
