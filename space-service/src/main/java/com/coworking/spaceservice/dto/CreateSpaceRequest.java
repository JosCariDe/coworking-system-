package com.coworking.spaceservice.dto;

import com.coworking.spaceservice.model.Space;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateSpaceRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;
    
    @NotNull(message = "Type is required")
    private Space.SpaceType type;
    
    private Set<String> amenities;
    
    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;
    
    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
    
    @NotNull(message = "Price per hour is required")
    @Positive(message = "Price per hour must be positive")
    private BigDecimal pricePerHour;
    
    private String description;
}

