package com.backend.dogwalks.exception.custom_exception;

public class UserAlreadyExsistException extends RuntimeException {
    public UserAlreadyExsistException(String message) {
        super(message);
    }
}
