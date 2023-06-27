package com.example.soundcloud.service;

import com.example.soundcloud.models.entities.Comment;
import com.example.soundcloud.models.entities.Playlist;
import com.example.soundcloud.models.entities.Song;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class AbstractService {

    @Autowired
    protected SongRepository songRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected ModelMapper songModelMapper;
    @Autowired
    protected PlaylistRepository playlistRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected Utility utility;
    @Autowired
    protected ListenedRepository listenedRepository;
    @Autowired
    protected JavaMailSender mailSender;

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User does not exist!"));
    }

    public Song findSongById(long songId) {
        return songRepository.findById(songId).orElseThrow(() -> new NotFoundException("Song does not exist!"));
    }

    public Playlist findPlaylistById(long playlistId) {
        return playlistRepository.findById(playlistId).orElseThrow(() -> new NotFoundException("Playlist does not exist!"));
    }

    public Comment findCommentById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment does not exist!"));
    }

    protected void sendEmailToFollowersWhenUpload(Song newSong, User uploader) {
        Song song = newSong;
        User user = uploader;
        for (User u : uploader.getFollowers()) {
            sendEmailForNewUpload(u, song);
        }
    }
//TODO da premestq dvata metoda v songService
    private void sendEmailForNewUpload(User user, Song song) {
        new Thread(() -> {
            try {
                String toAddress = user.getEmail();
                System.out.println(user.getEmail());
                String fromAddress = "soundcloudtests14@gmail.com";
                String senderName = "Sound Cloud";
                String subject = user.getUsername() + " has uploaded new track. Check it out!";
                String content = "Dear [[name]],<br>"
                        + user.getUsername() + " has upload new track. U can listen it up on the link bellow:<br>"
                        + "<h3><a href=\"[[URL]]\" target=\"_self\">CLICK HERE</a></h3>"
                        + "Hope you're having great time in Sound Cloud," +
                        "Sound Cloud";
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(fromAddress, senderName);
                helper.setTo(toAddress);
                helper.setSubject(subject);
                content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
                helper.setText(content, true);
                String verifyURL = "http://localhost:9999/songs/" + song.getId() + "/play";
                content = content.replace("[[URL]]", verifyURL);
                helper.setText(content, true);
                mailSender.send(message);
            } catch (MessagingException e) {
                throw new BadRequestException(e.getMessage(), e);
            } catch (UnsupportedEncodingException e) {
                throw new BadRequestException(e.getMessage(), e);
            }
        }).start();
    }

}
