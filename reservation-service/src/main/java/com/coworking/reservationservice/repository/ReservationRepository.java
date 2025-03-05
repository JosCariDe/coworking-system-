package com.coworking.reservationservice.repository;

import com.coworking.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findBySpaceId(Long spaceId);
    
    @Query("SELECT r FROM Reservation r WHERE r.spaceId = :spaceId AND " +
           "((r.startTime <= :endTime AND r.endTime >= :startTime) OR " +
           "(r.startTime >= :startTime AND r.startTime < :endTime)) AND " +
           "r.status != 'CANCELLED'")
    List<Reservation> findOverlappingReservations(
            @Param("spaceId") Long spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}

