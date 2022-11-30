package com.example.soundcloud.controllers;


import com.example.soundcloud.models.dto.ErrorDTO;
import com.example.soundcloud.models.exceptions.*;
import com.example.soundcloud.models.repositories.PlaylistRepository;
import com.example.soundcloud.models.repositories.UserRepository;
import com.example.soundcloud.service.CommentService;
import com.example.soundcloud.service.PlaylistService;
import com.example.soundcloud.service.SongService;
import com.example.soundcloud.service.UserService;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.parser.Entity;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
public abstract class GlobalController {

    public static final String LOGGED = "LOGGED";
    public static final String USER_ID = "USER_ID";
    public static final String REMOTE_IP = "REMOTE_IP";
    private static final String FAILED_LOG = "FAILED_LOG";
    private static final String USER_NAME = "USER_NAME";
    private ConcurrentHashMap<String, LocalDateTime> blockedUsers = new ConcurrentHashMap<>();

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected PlaylistRepository playlistRepository;
    @Autowired
    protected PlaylistService playlistService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected SongService songService;
    @Autowired
    protected CommentService commentService;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorDTO handlerNotFound(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorDTO handlerBadRequest(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private ErrorDTO handlerUnauthorized(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ErrorDTO handlerForbidden(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    private ErrorDTO handlerMethodNotAllowed(Exception ex) {
        return buildErrorInfo(ex, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleAllOthers(Exception e) {
        return buildErrorInfo(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleSQLException(SQLException e) {
        return buildErrorInfo(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleSQLException(FileException e) {
        return buildErrorInfo(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO buildErrorInfo(Exception e, HttpStatus status) {
        e.printStackTrace();
        ErrorDTO dto = new ErrorDTO();
        dto.setStatusCode(status.value());
        dto.setMessage(e.getMessage());
        dto.setTime(LocalDateTime.now());
        return dto;
    }


    public void logUser(HttpServletRequest req, long id) {
        HttpSession session = req.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, id);
        session.setAttribute(REMOTE_IP, req.getRemoteAddr());
        session.setMaxInactiveInterval(30 * 60);
    }

    public void logChecker(HttpServletRequest req, String username, int failedLog){
        HttpSession session = req.getSession();
        session.setAttribute(USER_NAME, username);
        session.setAttribute(FAILED_LOG, failedLog);
        blockAccount(session, username);
    }

    @SneakyThrows
    public void blockAccount(HttpSession session, String username){
        if ((int)session.getAttribute(FAILED_LOG) >= 5){
            if (blockedUsers.containsKey(username)){
                return;
            }else {
                blockedUsers.put(username, LocalDateTime.now());
            }
        }
    }

    public boolean isBlocked(String username){
        if (blockedUsers.containsKey(username)){
            return true;
        }return false;
    }

    @Scheduled(cron = "0 0 */1 * * *")
    @Async
    public void removeBlock(){
        Iterator<Map.Entry<String, LocalDateTime>> it = blockedUsers.entrySet().iterator();
        while (it.hasNext()){
            if (LocalDateTime.now().isAfter(it.next().getValue().plus(1,ChronoUnit.MINUTES))){
                it.remove();
            }
        }
    }


    public long getLoggedUserId(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        if (session.isNew() ||
                session.getAttribute(LOGGED) == null ||
                (!(boolean) session.getAttribute(LOGGED)) ||
                !session.getAttribute(REMOTE_IP).equals(ip)) {
            throw new UnauthorizedException("You have to login!");
        }
        return (long) session.getAttribute(USER_ID);
    }
}