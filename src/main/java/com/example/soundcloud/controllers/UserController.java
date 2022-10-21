package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import com.example.soundcloud.service.UserService;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.List;

@RestController
public class UserController extends GlobalController {
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserWithoutPDTO register(@RequestBody RegisterDTO user) {
        return userService.register(user);
    }

    @GetMapping("/users")
    public List<UserWithoutPDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserWithoutPDTO getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users/verify")
    public String verifyAccount(@RequestBody VerifyDTO dto, HttpServletRequest req){
        long userId = getLoggedUserId(req);
        return userService.verifyAccount(userId, dto);
    }

    @GetMapping("/users/{id}/songs")
    public UserWithoutPWithSongsDTO getUserSongsById(@PathVariable long id){
        return userService.getUserSongsById(id);
    }

    @PostMapping("/auth")
    public UserWithoutPDTO logIn(@RequestBody LoginDTO dto, HttpServletRequest req) {
        UserWithoutPDTO user = userService.login(dto);
        HttpSession session = req.getSession();
        if (session.getAttribute("LOGGED") != null) {
            throw new MethodNotAllowedException("You are already logged in as " + user.getFirstName() +
                    " " + user.getLastName() + "," +
                    " you need to log out before logging in as different user. ");
        }
        if (user != null) {
            logUser(req, user.getId());
            return user;
        } else {
            throw new UnauthorizedException("Wrong username or password!");
        }
    }

    @PutMapping("/users")
    public UserWithoutPDTO editProfile(@RequestBody EditDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.editProfile(dto, userId);
    }

    @PostMapping("/logout")
    public String logOut(@RequestBody LogOut dto, HttpServletRequest req) {
        HttpSession session = req.getSession();
        long userId = getLoggedUserId(req);
        session.invalidate();
        return userService.logOut(userId);
    }

    @DeleteMapping("/users")
    public String deleteUser(@RequestBody DeleteDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.deleteUser(userId, dto);
    }

    @PutMapping("/users/pass")
    public String changePW(@RequestBody ChangePDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.changePW(dto, userId);
    }

    @PostMapping("/users/uploads")
    public String uploadProfileImage(@RequestParam(value = "file") MultipartFile file, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.uploadProfileImage(file, userId);
    }

    @PutMapping("/users/uploads")
    public String deleteProfileImage(HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.deleteProfileImage(userId);

    }
}
