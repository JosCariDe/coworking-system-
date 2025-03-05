package com.coworking.spaceservice.controller;

import com.coworking.spaceservice.dto.CreateSpaceRequest;
import com.coworking.spaceservice.dto.SpaceDto;
import com.coworking.spaceservice.model.Space;
import com.coworking.spaceservice.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceController {
    
    private final SpaceService spaceService;
    
    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpaces() {
        return ResponseEntity.ok(spaceService.getAllSpaces());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SpaceDto> getSpaceById(@PathVariable Long id) {
        return ResponseEntity.ok(spaceService.getSpaceById(id));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SpaceDto>> getSpacesByType(@PathVariable Space.SpaceType type) {
        return ResponseEntity.ok(spaceService.getSpacesByType(type));
    }
    
    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<SpaceDto>> getSpacesByMinCapacity(@PathVariable Integer capacity) {
        return ResponseEntity.ok(spaceService.getSpacesByMinCapacity(capacity));
    }
    
    @PostMapping
    public ResponseEntity<SpaceDto> createSpace(@Valid @RequestBody CreateSpaceRequest createSpaceRequest) {
        return new ResponseEntity<>(spaceService.createSpace(createSpaceRequest), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SpaceDto> updateSpace(@PathVariable Long id, 
                                              @Valid @RequestBody CreateSpaceRequest updateSpaceRequest) {
        return ResponseEntity.ok(spaceService.updateSpace(id, updateSpaceRequest));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.noContent().build();
    }
}

