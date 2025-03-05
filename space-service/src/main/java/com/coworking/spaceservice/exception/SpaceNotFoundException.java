package com.coworking.spaceservice.exception;

public class SpaceNotFoundException extends RuntimeException {
    
    public SpaceNotFoundException(String message) {
        super(message);
    }
}

