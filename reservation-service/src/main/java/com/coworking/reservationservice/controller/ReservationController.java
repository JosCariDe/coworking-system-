package com.coworking.reservationservice.controller;

import com.coworking.reservationservice.dto.CreateReservationRequest;
import com.coworking.reservationservice.dto.ReservationDto;
import com.coworking.reservationservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }
    
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<ReservationDto>> getReservationsBySpaceId(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reservationService.getReservationsBySpaceId(spaceId));
    }
    
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody CreateReservationRequest createReservationRequest) {
        return new ResponseEntity<>(reservationService.createReservation(createReservationRequest), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id, 
                                                          @Valid @RequestBody CreateReservationRequest updateReservationRequest) {
        return ResponseEntity.ok(reservationService.updateReservation(id, updateReservationRequest));
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}

