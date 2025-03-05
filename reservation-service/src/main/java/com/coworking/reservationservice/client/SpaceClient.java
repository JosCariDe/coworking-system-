package com.coworking.reservationservice.client;

import com.coworking.reservationservice.dto.SpaceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "space-service")
public interface SpaceClient {
    
    @GetMapping("/api/spaces/{id}")
    SpaceDto getSpaceById(@PathVariable("id") Long id);
}

