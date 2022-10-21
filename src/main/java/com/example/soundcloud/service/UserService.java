package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.song.SongWithoutUserDTO;
import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import com.example.soundcloud.models.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
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

    public UserWithoutPDTO register(RegisterDTO userDTO) {
        if (utility.validateRegistration(userDTO)) {
            userDTO.setCreatedAt(LocalDateTime.now());
            User user = modelMapper.map(userDTO, User.class);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return modelMapper.map(user, UserWithoutPDTO.class);
        }
        return null;
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
                System.out.println(user.getSongs().size());
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
}
