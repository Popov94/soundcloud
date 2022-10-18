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
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private Utility utility;

    public UserWithoutPDTO register(RegisterDTO u) {
        if (utility.validateRegistration(u)) {
            u.setCreatedAt(LocalDateTime.now());
            User u1 = modelMapper.map(u, User.class);
            u1.setPassword(bCryptPasswordEncoder.encode(u1.getPassword()));
            userRepository.save(u1);
            return modelMapper.map(u1, UserWithoutPDTO.class);
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
            User u = user.get();
            if (bCryptPasswordEncoder.matches(password, u.getPassword())) {
                u.setLastLogin(LocalDateTime.now());
                userRepository.save(u);
                return modelMapper.map(u, UserWithoutPDTO.class);
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
            User u = user.get();
            UserWithoutPDTO dto = modelMapper.map(u, UserWithoutPDTO.class);
            return dto;
        } else {
            throw new NotFoundException("User does not exist!");
        }
    }

    public List<UserWithoutPDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserWithoutPDTO.class)).collect(Collectors.toList());
    }

    public UserWithoutPDTO editProfile(EditDTO dto, long id) {
        System.out.println(id);
        User u1 = userRepository.findById(id).orElseThrow(() -> new MethodNotAllowedException("User initials are wrong!"));
        System.out.println(u1);
        System.out.println(id);
        if (utility.editProfileValidation(dto)) {
            if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), u1.getPassword())) {
                User u = setEdit(dto, u1);
                userRepository.save(u);
                return modelMapper.map(u, UserWithoutPDTO.class);
            } else {
                throw new MethodNotAllowedException("Password is wrong");
            }
        } else {
            throw new BadRequestException("Invalid input!");
        }
    }

    private User setEdit(EditDTO dto, User u1) {
        System.out.println(u1);
        u1.setEmail(dto.getEmail());
        u1.setLastName(dto.getLastName());
        u1.setFirstName(dto.getFirstName());
        u1.setAddress(dto.getAddress());
        u1.setCity(dto.getCity());
        u1.setDateOfBirthday(dto.getDateOfBirthday());
        u1.setGender(dto.getGender());
        u1.setUsername(dto.getUsername());
        return u1;
    }

    public String logOut(HttpSession session) {
        long x = (Long) session.getAttribute("USER_ID");
        User name = null;
        name = userRepository.findById(x).get();
        session.invalidate();
        return "Mr. " + name.getLastName() + " you have been logged out successfully!";
    }

    public String deleteUser(HttpSession session, DeleteDTO dto) {
        long x = (long) session.getAttribute("USER_ID");
        User u = userRepository.findById(x).get();
        if (bCryptPasswordEncoder.matches(dto.getPassword(), u.getPassword())) {
            userRepository.deleteById(u.getId());
            return "User: " + u.getUsername() + " has been successfully deleted";
        } else {
            throw new UnauthorizedException("Password does not match!");
        }
    }

    public String changePW(ChangePDTO dto, HttpSession session) {
        long x = (long) session.getAttribute("USER_ID");
        User u = userRepository.findById(x).get();
        if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), u.getPassword())) {
            if (dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                u.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
                userRepository.save(u);
                return "Your password have been changed successfully!";
            } else {
                throw new BadRequestException("New passwords does not match!");
            }
        } else {
            throw new BadRequestException("Password is incorrect!");
        }
    }
}
