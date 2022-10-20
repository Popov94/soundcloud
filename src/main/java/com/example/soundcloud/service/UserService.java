package com.example.soundcloud.service;

import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.NotFoundException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import com.example.soundcloud.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
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

    public UserWithoutPDTO register(RegisterDTO user) {
        if (utility.validateRegistration(user)) {
            user.setCreatedAt(LocalDateTime.now());
            User user1 = modelMapper.map(user, User.class);
            user1.setPassword(bCryptPasswordEncoder.encode(user1.getPassword()));
            userRepository.save(user1);
            return modelMapper.map(user1, UserWithoutPDTO.class);
        }
        return null;
    }

    public UserWithoutPDTO login(LoginDTO dto) {
        if (!utility.userExist(dto.getUsername())) {
            throw new UnauthorizedException("Wrong username or password!");
        }
        String password = dto.getPassword();
        String username = dto.getUsername();
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isPresent()) {
            User user1 = user.get();
            if (bCryptPasswordEncoder.matches(password, user1.getPassword())) {
                user1.setLastLogin(LocalDateTime.now());
                userRepository.save(user1);
                return modelMapper.map(user1, UserWithoutPDTO.class);
            } else {
                throw new UnauthorizedException("Wrong username or password!");
            }
        } else {
            throw new UnauthorizedException("Wrong username or password!");
        }
    }

    public UserWithoutPDTO getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User user1 = user.get();
            UserWithoutPDTO dto = modelMapper.map(user1, UserWithoutPDTO.class);
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
        if (utility.editProfileValidation(dto)) {
            if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                User user1 = setEdit(dto, user);
                userRepository.save(user1);
                return modelMapper.map(user1, UserWithoutPDTO.class);
            } else {
                throw new MethodNotAllowedException("Password is wrong");
            }
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    private User setEdit(EditDTO dto, User user) {
        user.setEmail(dto.getEmail());
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setDateOfBirthday(dto.getDateOfBirthday());
        user.setGender(dto.getGender());
        user.setUsername(dto.getUsername());
        return user;
    }

    public String logOut(long userId) {
        User user = findUserById(userId);
        return "Mr. " + user.getLastName() + " you have been logged out successfully!";
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
}
