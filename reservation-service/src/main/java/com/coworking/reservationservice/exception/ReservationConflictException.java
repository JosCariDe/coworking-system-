package com.coworking.reservationservice.exception;

public class ReservationConflictException extends RuntimeException {
    
    public ReservationConflictException(String message) {
        super(message);
    }
}

