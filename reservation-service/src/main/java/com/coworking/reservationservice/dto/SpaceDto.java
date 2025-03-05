package com.coworking.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceDto {
    private Long id;
    private String name;
    private Integer capacity;
    private String type;
    private Set<String> amenities;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private BigDecimal pricePerHour;
    private String description;
    private boolean active;
}

