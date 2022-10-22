package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class UserService extends AbstractService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private Utility utility;

    @Autowired
    private JavaMailSender mailSender;

    @SneakyThrows
    public UserWithoutPDTO register(RegisterDTO userDTO) {
        if (utility.validateRegistration(userDTO)) {
            userDTO.setCreatedAt(LocalDateTime.now());
            User user = modelMapper.map(userDTO, User.class);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            String verificationCode = RandomString.make(64);
            user.setVerificationCode(verificationCode);
            sendVerificationEmail(user);
            userRepository.save(user);
            return modelMapper.map(user, UserWithoutPDTO.class);
        }
        return null;
    }

    private void sendVerificationEmail(User user)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "soundcloudtests14@gmail.com";
        String senderName = "Sound Cloud";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please use the generated code below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">CODE: " + user.getVerificationCode() + " </a></h3>"
                + "Thank you,<br>"
                + "Sound Cloud-IT Talents s14.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        helper.setText(content, true);
        mailSender.send(message);
    }

    public UserWithoutPDTO login(LoginDTO dto) {
        if (!utility.userExist(dto.getUsername())) {
            throw new UnauthorizedException("Wrong username or password!");
        }
        String password = dto.getPassword();
        String username = dto.getUsername();
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return modelMapper.map(user, UserWithoutPDTO.class);
            } else {
                throw new UnauthorizedException("Wrong username or password!");
            }
        } else {
            throw new UnauthorizedException("Wrong username or password!");
        }
    }

    public UserWithoutPDTO getUserById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserWithoutPDTO dto = modelMapper.map(user, UserWithoutPDTO.class);
            return dto;
        } else {
            throw new NotFoundException("User does not exist!");
        }
    }

    public List<UserWithoutPDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserWithoutPDTO.class)).collect(Collectors.toList());
    }

    public UserWithoutPDTO editProfile(EditDTO dto, long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new MethodNotAllowedException("User initials are wrong!"));
        if (utility.editProfileValidation(dto, id)) {
            if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                setEdit(dto, user);
                userRepository.save(user);
                return modelMapper.map(user, UserWithoutPDTO.class);
            } else {
                throw new MethodNotAllowedException("Password is wrong");
            }
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    private void setEdit(EditDTO dto, User user) {
        user.setEmail(dto.getEmail());
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setDateOfBirthday(dto.getDateOfBirthday());
        user.setGender(dto.getGender());
        user.setUsername(dto.getUsername());
    }

    public String logOut(long userId) {
        User user = findUserById(userId);
        if (user.getGender().equals("Male")) {
            return "Mr. " + user.getLastName() + " you have been logged out successfully!";
        } else {
            return "Mrs. " + user.getLastName() + " you have been logged out successfully!";
        }
    }

    public String deleteUser(long userId, DeleteDTO dto) {
        User user = findUserById(userId);
        if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            userRepository.deleteById(user.getId());
            return "User: " + user.getUsername() + " has been successfully deleted";
        } else {
            throw new UnauthorizedException("Password does not match!");
        }
    }

    public String changePW(ChangePDTO dto, long userId) {
        User user = findUserById(userId);
        if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            if (dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
                userRepository.save(user);
                return "Your password have been changed successfully!";
            } else {
                throw new BadRequestException("New passwords does not match!");
            }
        } else {
            throw new BadRequestException("Password is incorrect!");
        }
    }

    public String uploadProfileImage(MultipartFile file, long userId) {
        if (utility.profileImageValidation(file)) {
            try {
                User user = findUserById(userId);
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String name = "uploads" + File.separator + System.nanoTime() + userId + "." + extension;
                File f = new File(name);
                if (!f.exists()) {
                    Files.copy(file.getInputStream(), f.toPath());
                } else {
                    throw new BadRequestException("The file already exist. This should never happen. Call the service!");
                }
                if (user.getProfileImageUrl() != null) {
                    File oldFile = new File(user.getProfileImageUrl());
                    oldFile.delete();
                }
                user.setProfileImageUrl(name);
                userRepository.save(user);
                return name;
            } catch (IOException exception) {
                throw new BadRequestException(exception.getMessage(), exception);
            }
        } else {
            throw new BadRequestException("Only images can be uploaded, and they must be under 5MB!");
        }
    }

    public String deleteProfileImage(long userId) {
        User user = findUserById(userId);
        File profileImage = new File(user.getProfileImageUrl());
        profileImage.delete();
        return "Your profile picture has been removed!";
    }

    public UserWithoutPWithSongsDTO getUserSongsById(long id) {
        User user = findUserById(id);
        UserWithoutPWithSongsDTO dto = modelMapper.map(user, UserWithoutPWithSongsDTO.class);
        dto.setSongs(user.getSongs().stream().map(song -> modelMapper.map(song, SongWithoutUserDTO.class)).collect(Collectors.toList()));
        return dto;
    }

    public String verifyAccount(VerifyDTO dto) {
        Optional<User> optionalUser = userRepository.findUserByVerificationCode(dto.getCode());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isVerified()){
                throw  new BadRequestException("You already verified yourself!");
            }
            user.setVerified(true);
            userRepository.save(user);
            return "You have been verified successfully";
        } else {
            throw new BadRequestException("Code is wrong!");
        }

    }

    public List<UserWithoutPWithSongsDTO> getUserByName(String userName) {
        List<User> users = userRepository.findByKeyword(userName).stream().collect(Collectors.toList());
        List<UserWithoutPWithSongsDTO> dto = users.
                stream().
                map(user -> modelMapper.map(user, UserWithoutPWithSongsDTO.class)).
                collect(Collectors.toList());
        return dto;
    }

    public boolean isUserVerified(long userId) {
        User user = findUserById(userId);
        if (user.isVerified()) {
            return true;
        } else {
            return false;
        }
    }

    public String followUser(long followerId, long followedId) {
        User followedUser = findUserById(followedId);
        User follower = findUserById(followerId);
        if (followedId == followerId) {
            throw new BadRequestException("You can not follow yourself!");
        }
        for (Long l : userRepository.getFollowingUsersIds(followerId)) {
            if (l.equals(followedId)) {
                throw new BadRequestException("You already followed this user!");
            }
        }
        follower.getFollowing().add(followedUser);
        userRepository.save(follower);
        return "You followed successfully " + followedUser.getFirstName() + " " + followedUser.getLastName();
    }

    public String unfollowUser(long followerId, long followedId) {
        if (followedId == followerId){
            throw new BadRequestException("You can not unfollow yourself!");
        }

        for (Long l : userRepository.getFollowingUsersIds(followerId)){
            if (!l.equals(followedId)){
                System.out.println(l);
                throw new BadRequestException("You can not unfollow user which u are not following");
            }
        }
        System.out.println("zashto");
        User followedUser = findUserById(followedId);
        User follower = findUserById(followerId);
        follower.getFollowing().remove(followedUser);
        userRepository.save(follower);
        return "You have unfollowed " + followedUser.getFirstName() + " " + followedUser.getLastName();
    }
}
