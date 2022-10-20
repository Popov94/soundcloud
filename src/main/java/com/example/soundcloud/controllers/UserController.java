package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import com.example.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController extends GlobalController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
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

    @PostMapping("/auth")
    public UserWithoutPDTO logIn(@RequestBody LoginDTO dto, HttpServletRequest req) {
        UserWithoutPDTO user = userService.login(dto);
        if (user != null) {
            logUser(req, user.getId());
            return user;
        } else {
            throw new UnauthorizedException("Wrong username or password!");
        }
    }

    @PutMapping("/users/edit_profile")
    public UserWithoutPDTO editProfile(@RequestBody EditDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.editProfile(dto, userId);
    }

    @PostMapping("/auth1")
    public String logOut(@RequestBody LogOut dto, HttpServletRequest req) {
        HttpSession session = req.getSession();
        long userId = getLoggedUserId(req);
        session.invalidate();
        return userService.logOut(userId);
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestBody DeleteDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.deleteUser(userId, dto);
    }

    @PutMapping("/users/edit_profile/change_pw")
    public String changePW(@RequestBody ChangePDTO dto, HttpServletRequest req) {
        long userId = getLoggedUserId(req);
        return userService.changePW(dto, userId);
    }
}
