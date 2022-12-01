package com.example.soundcloud.controllers;

import com.example.soundcloud.models.dto.song.ResponseSongDTO;
import com.example.soundcloud.models.dto.user.*;
import com.example.soundcloud.models.exceptions.BadRequestException;
import com.example.soundcloud.models.exceptions.MethodNotAllowedException;
import com.example.soundcloud.models.exceptions.UnauthorizedException;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class UserController extends GlobalController {

    private ConcurrentHashMap<String,Integer> logManager = new ConcurrentHashMap<>();
    private int counter = 0;

    @PostMapping("/users")
    public UserWithoutPDTO register(@RequestBody RegisterDTO user, HttpServletRequest request) {
        return userService.register(user, getRequestSiteURL(request));
    }

    private String getRequestSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/users/{id}")
    public UserWithoutPDTO getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code){
        return userService.verifyAccount(code);
    }

    @GetMapping("/users/{id}/songs")
    public UserWithoutPWithSongsDTO getUserSongsById(@PathVariable long id) {
        return userService.getUserSongsById(id);
    }

    private void checkLog(HttpServletRequest req, LoginDTO dto,HttpServletResponse resp) {
        if (!userService.checkLog(dto)) {
            String username = dto.getUsername();
            if (!logManager.containsKey(username)) {
                logManager.put(username,1);
            } else {
                logManager.put(username, logManager.get(username)+1);
                logChecker(req, username, logManager.get(username));
            }
        }
    }

    @PostMapping("/auth")
    @SneakyThrows
    public UserWithoutPDTO logIn(@RequestBody LoginDTO dto, HttpServletRequest req, HttpServletResponse resp) {
        if (isBlocked(dto.getUsername())){
            throw new UnauthorizedException("You have reached the limit of tries to log in. Try again after 12 hour!");
        }
        HttpSession session = req.getSession();
        checkLog(req,dto,resp);
        UserWithoutPDTO user = userService.login(dto);
        if (session.getAttribute("LOGGED") != null) {
            throw new MethodNotAllowedException("You are already logged in as " + user.getFirstName() +
                    " " + user.getLastName() + "," +
                    " you need to log out before logging in as different user. ");
        }
        if (user != null) {
            logUser(req, user.getId());
            if (!userService.isUserVerified(user.getId())) {
                session.invalidate();
                throw new MethodNotAllowedException("You have to confirm your registration!");
            }
            logManager.remove(user.getUsername());
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

    @GetMapping("/users/new_password")
    public String forgotPassword(@RequestBody UserInfoDTO dto,HttpServletRequest request){
       return userService.forgotPassword(dto,getRequestSiteURL(request));
    }
    @PostMapping("/change_password")
    public String changeForgottenPassword(@RequestBody ChangeForgottenPWDTO dto, @Param("code") String code){
        return userService.ChangeForgottenPassword(dto,code);
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
