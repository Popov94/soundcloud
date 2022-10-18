package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.entities.User;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import com.example.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController extends GlobalUserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserWithoutPDTO register(@RequestBody RegisterDTO u) {
        return userService.register(u);
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
    public UserWithoutPDTO logIn(@RequestBody LoginDTO dto, HttpSession session) {
        UserWithoutPDTO user = userService.login(dto);
        if (user != null) {
            session.setAttribute("LOGGED", true);
            session.setAttribute("USER_ID", user.getId());
            session.setMaxInactiveInterval(60 * 60);//1hour
            return user;
        } else {
            throw new UnauthorizedException("Wrong username or password");
        }
    }


    @PutMapping("/users/edit_profile")
    public UserWithoutPDTO editProfile(@RequestBody EditDTO dto, HttpSession session) {
        long x = 0;
        if (session.getAttribute("LOGGED") != null) {
            x = (long) session.getAttribute("USER_ID");
            System.out.println(x);
            return userService.editProfile(dto, x);
        } else {
            throw new UnauthorizedException("You must be logged in!");
        }

    }

    @PostMapping("/auth1")
    public String logOut(@RequestBody LogOut dto, HttpSession session) {
        if (session.getAttribute("LOGGED") == null) {
            throw new UnauthorizedException("You must be logged in!");
        }
        return userService.logOut(session);
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestBody DeleteDTO dto, HttpSession session) {
        if (session.getAttribute("LOGGED") == null) {
            throw new UnauthorizedException("You must be logged in!");
        }
        return userService.deleteUser(session, dto);
    }

    @PutMapping("/users/edit_profile/change_pw")
    public String changePW(@RequestBody ChangePDTO dto, HttpSession session) {
        if (session.getAttribute("LOGGED") == null) {
            throw new UnauthorizedException("You must be logged in!");
        }
        return userService.changePW(dto, session);
    }


}
