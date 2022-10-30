package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.song.ResponseSongDTO;
import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserController extends GlobalController {

    @PostMapping("/users")
    public UserWithoutPDTO register(@RequestBody RegisterDTO user) {
        return userService.register(user);
    }

//    @GetMapping("/users")
//    public List<UserWithoutPDTO> getAllUsers() {
//        return userService.getAllUsers();
//    }

    @GetMapping("/users/{id}")
    public UserWithoutPDTO getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users/verify")
    public String verifyAccount(@RequestBody VerifyDTO dto) {
        return userService.verifyAccount(dto);
    }

    @GetMapping("/users/{id}/songs")
    public UserWithoutPWithSongsDTO getUserSongsById(@PathVariable long id) {
        return userService.getUserSongsById(id);
    }

    @PostMapping("/auth")
    @SneakyThrows
    public UserWithoutPDTO logIn(@RequestBody LoginDTO dto, HttpServletRequest req, HttpServletResponse resp) {
        UserWithoutPDTO user = userService.login(dto);
        HttpSession session = req.getSession();
        if (session.getAttribute("LOGGED") != null) {
            throw new MethodNotAllowedException("You are already logged in as " + user.getFirstName() +
                    " " + user.getLastName() + "," +
                    " you need to log out before logging in as different user. ");
        }
        if (user != null) {
            logUser(req, user.getId());
            if (!userService.isUserVerified(user.getId())) {
                session.invalidate();
//                String[] text = user.getEmail().split("@");
//                String url = "https://" + text[text.length-1];
//                resp.sendRedirect(url);
                throw new MethodNotAllowedException("You have to confirm your registration!");
            }
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
    public String logOut(HttpServletRequest req) {
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

    //return user with his songs or without??
    @GetMapping("/users/search/{userName}")
    public List<UserWithoutPWithSongsDTO> getUserByName(@PathVariable String userName) {
        return userService.getUserByName(userName);
    }

    @PostMapping("/users/{followedId}/follow")
    public String followUser(@PathVariable long followedId, HttpServletRequest req) {
        long followerId = getLoggedUserId(req);
        return userService.followUser(followerId, followedId);
    }

    @DeleteMapping("/users/{followedId}/unfollow")
    public String unfollowUser(@PathVariable long followedId, HttpServletRequest req) {
        long followerId = getLoggedUserId(req);
        return userService.unfollowUser(followerId, followedId);
    }


    @GetMapping("/users/pagination/{offset}/{pageSize}")
    public APIResponse<Page<UserWithoutPDTO>> findAllUserWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
        Page<UserWithoutPDTO> users = userService.findAllUsersWithPagination(offset, pageSize);
        return new APIResponse<>(users.getSize(), users);
    }

    @GetMapping("/users/pagination/{offset}/{pageSize}/{sortedBy}")
    public APIResponse<Page<UserWithoutPDTO>> findAllUserWithPaginationAndSorting(@PathVariable int offset,
                                                                                  @PathVariable int pageSize,
                                                                                  @PathVariable String sortedBy) {
        Page<UserWithoutPDTO> users = userService.findAllUsersWithPaginationAndSorting(offset, pageSize, sortedBy);
        return new APIResponse<>(users.getSize(), users);
    }

    @GetMapping("/users/pagination/{offset}/{pageSize}/{sortedBy}/desc")
    public APIResponse<Page<UserWithoutPDTO>> findAllUserWithPaginationAndSortingDesc(@PathVariable int offset,
                                                                                      @PathVariable int pageSize,
                                                                                      @PathVariable String sortedBy) {
        Page<UserWithoutPDTO> users = userService.findAllUsersWithPaginationAndSortingDesc(offset, pageSize, sortedBy);
        return new APIResponse<>(users.getSize(), users);
    }

    @GetMapping("/home")
    public HashMap<String, List<ResponseSongDTO>> homePage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute(LOGGED) != null) {
            long userId = (long) session.getAttribute(USER_ID);
            return userService.homePageForLogged(userId);
        } else {
            return userService.homePageForNonLoged();
        }
    }

}
