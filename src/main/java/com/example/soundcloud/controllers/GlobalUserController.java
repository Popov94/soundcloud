package com.example.soundcloud.controllers;


import com.example.soundcloud.models.dto.ErrorDTO;
import com.example.soundcloud.models.exceptions.*;
import com.example.soundcloud.models.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public abstract class GlobalUserController {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ModelMapper modelMapper;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorDTO exceptionHandler1(Exception ex){
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(ex.getMessage());
        dto.setStatusCode(HttpStatus.NOT_FOUND.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorDTO exceptionHandler2(Exception ex){
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(ex.getMessage());
        dto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private ErrorDTO exceptionHandler3(Exception ex){
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(ex.getMessage());
        dto.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ErrorDTO exceptionHandler4(Exception ex){
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(ex.getMessage());
        dto.setStatusCode(HttpStatus.FORBIDDEN.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    private ErrorDTO exceptionHandler5(Exception ex){
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(ex.getMessage());
        dto.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

}
