package com.coworking.reservationservice.mapper;

import com.coworking.reservationservice.dto.CreateReservationRequest;
import com.coworking.reservationservice.dto.ReservationDto;
import com.coworking.reservationservice.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface ReservationMapper {
    
    ReservationDto toDto(Reservation reservation);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Reservation toEntity(CreateReservationRequest createReservationRequest);
}

