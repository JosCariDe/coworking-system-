package com.coworking.spaceservice.exception;

public class SpaceAlreadyExistsException extends RuntimeException {
    
    public SpaceAlreadyExistsException(String message) {
        super(message);
    }
}

