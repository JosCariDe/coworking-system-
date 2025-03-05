package com.coworking.spaceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceDto {
    private Long id;
    private String name;
    private String type;
    private Integer capacity;
    private String location;
}

