package com.coworking.reservationservice.service;

import com.coworking.reservationservice.client.SpaceClient;
import com.coworking.reservationservice.client.UserClient;
import com.coworking.reservationservice.dto.CreateReservationRequest;
import com.coworking.reservationservice.dto.ReservationDto;
import com.coworking.reservationservice.dto.SpaceDto;
import com.coworking.reservationservice.dto.UserDto;
import com.coworking.reservationservice.exception.ReservationConflictException;
import com.coworking.reservationservice.exception.ReservationNotFoundException;
import com.coworking.reservationservice.exception.ResourceNotFoundException;
import com.coworking.reservationservice.mapper.ReservationMapper;
import com.coworking.reservationservice.model.Reservation;
import com.coworking.reservationservice.repository.ReservationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserClient userClient;
    private final SpaceClient spaceClient;
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::enrichReservationWithDetails)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
        
        return enrichReservationWithDetails(reservation);
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        try {
            // Verify user exists
            userClient.getUserById(userId);
            
            return reservationRepository.findByUserId(userId)
                    .stream()
                    .map(this::enrichReservationWithDetails)
                    .collect(Collectors.toList());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsBySpaceId(Long spaceId) {
        try {
            // Verify space exists
            spaceClient.getSpaceById(spaceId);
            
            return reservationRepository.findBySpaceId(spaceId)
                    .stream()
                    .map(this::enrichReservationWithDetails)
                    .collect(Collectors.toList());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Space not found with id: " + spaceId);
        }
    }
    
    @Transactional
    public ReservationDto createReservation(CreateReservationRequest createReservationRequest) {
        // Validate user exists
        UserDto userDto;
        try {
            userDto = userClient.getUserById(createReservationRequest.getUserId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + createReservationRequest.getUserId());
        }
        
        // Validate space exists and check opening hours
        SpaceDto spaceDto;
        try {
            spaceDto = spaceClient.getSpaceById(createReservationRequest.getSpaceId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Space not found with id: " + createReservationRequest.getSpaceId());
        }
        
        // Validate reservation times
        validateReservationTimes(createReservationRequest, spaceDto);
        
        // Check for conflicts
        List<Reservation> conflictingReservations = reservationRepository.findOverlappingReservations(
                createReservationRequest.getSpaceId(),
                createReservationRequest.getStartTime(),
                createReservationRequest.getEndTime()
        );
        
        if (!conflictingReservations.isEmpty()) {
            throw new ReservationConflictException("The space is already reserved for the requested time period");
        }
        
        Reservation reservation = reservationMapper.toEntity(createReservationRequest);
        Reservation savedReservation = reservationRepository.save(reservation);
        
        ReservationDto reservationDto = reservationMapper.toDto(savedReservation);
        reservationDto.setUserName(userDto.getName());
        reservationDto.setSpaceName(spaceDto.getName());
        
        return reservationDto;
    }
    
    @Transactional
    public ReservationDto updateReservation(Long id, CreateReservationRequest updateReservationRequest) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
        
        // Validate user exists
        UserDto userDto;
        try {
            userDto = userClient.getUserById(updateReservationRequest.getUserId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + updateReservationRequest.getUserId());
        }
        
        // Validate space exists and check opening hours
        SpaceDto spaceDto;
        try {
            spaceDto = spaceClient.getSpaceById(updateReservationRequest.getSpaceId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Space not found with id: " + updateReservationRequest.getSpaceId());
        }
        
        // Validate reservation times
        validateReservationTimes(updateReservationRequest, spaceDto);
        
        // Check for conflicts (excluding this reservation)
        List<Reservation> conflictingReservations = reservationRepository.findOverlappingReservations(
                updateReservationRequest.getSpaceId(),
                updateReservationRequest.getStartTime(),
                updateReservationRequest.getEndTime()
        );
        
        conflictingReservations = conflictingReservations.stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());
        
        if (!conflictingReservations.isEmpty()) {
            throw new ReservationConflictException("The space is already reserved for the requested time period");
        }
        
        // Update reservation
        reservation.setUserId(updateReservationRequest.getUserId());
        reservation.setSpaceId(updateReservationRequest.getSpaceId());
        reservation.setStartTime(updateReservationRequest.getStartTime());
        reservation.setEndTime(updateReservationRequest.getEndTime());
        reservation.setNotes(updateReservationRequest.getNotes());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        ReservationDto reservationDto = reservationMapper.toDto(updatedReservation);
        reservationDto.setUserName(userDto.getName());
        reservationDto.setSpaceName(spaceDto.getName());
        
        return reservationDto;
    }
    
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
        
        reservation.setStatus("CANCELLED");
        reservation.setUpdatedAt(LocalDateTime.now());
        
        reservationRepository.save(reservation);
    }
    
    private void validateReservationTimes(CreateReservationRequest request, SpaceDto spaceDto) {
        // Check if start time is before end time
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Check if reservation is within space opening hours
        LocalTime startTimeOfDay = request.getStartTime().toLocalTime();
        LocalTime endTimeOfDay = request.getEndTime().toLocalTime();
        
        if (startTimeOfDay.isBefore(spaceDto.getOpeningTime()) || 
                endTimeOfDay.isAfter(spaceDto.getClosingTime())) {
            throw new IllegalArgumentException(
                    "Reservation must be within space opening hours: " + 
                    spaceDto.getOpeningTime() + " - " + spaceDto.getClosingTime());
        }
    }
    
    private ReservationDto enrichReservationWithDetails(Reservation reservation) {
        ReservationDto reservationDto = reservationMapper.toDto(reservation);
        
        try {
            UserDto userDto = userClient.getUserById(reservation.getUserId());
            reservationDto.setUserName(userDto.getName());
        } catch (FeignException.NotFound e) {
            reservationDto.setUserName("Unknown User");
        }
        
        try {
            SpaceDto spaceDto = spaceClient.getSpaceById(reservation.getSpaceId());
            reservationDto.setSpaceName(spaceDto.getName());
        } catch (FeignException.NotFound e) {
            reservationDto.setSpaceName("Unknown Space");
        }
        
        return reservationDto;
    }
}

