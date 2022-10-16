package com.example.soundcloud.models.exceptions;

public class MethodNotAllowedException extends RuntimeException{

    public MethodNotAllowedException(String msg){
        super(msg);
    }
}
