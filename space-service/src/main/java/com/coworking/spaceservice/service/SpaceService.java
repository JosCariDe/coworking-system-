package com.coworking.spaceservice.service;

import com.coworking.spaceservice.dto.CreateSpaceRequest;
import com.coworking.spaceservice.dto.SpaceDto;
import com.coworking.spaceservice.exception.SpaceAlreadyExistsException;
import com.coworking.spaceservice.exception.SpaceNotFoundException;
import com.coworking.spaceservice.mapper.SpaceMapper;
import com.coworking.spaceservice.model.Space;
import com.coworking.spaceservice.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceService {
    
    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper;
    
    @Transactional(readOnly = true)
    public List<SpaceDto> getAllSpaces() {
        return spaceRepository.findByActiveTrue()
                .stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public SpaceDto getSpaceById(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new SpaceNotFoundException("Space not found with id: " + id));
        
        if (!space.isActive()) {
            throw new SpaceNotFoundException("Space not found with id: " + id);
        }
        
        return spaceMapper.toDto(space);
    }
    
    @Transactional(readOnly = true)
    public List<SpaceDto> getSpacesByType(Space.SpaceType type) {
        return spaceRepository.findByTypeAndActiveTrue(type)
                .stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SpaceDto> getSpacesByMinCapacity(Integer capacity) {
        return spaceRepository.findByCapacityGreaterThanEqualAndActiveTrue(capacity)
                .stream()
                .map(spaceMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SpaceDto createSpace(CreateSpaceRequest createSpaceRequest) {
        if (spaceRepository.existsByNameAndActiveTrue(createSpaceRequest.getName())) {
            throw new SpaceAlreadyExistsException("Space already exists with name: " + createSpaceRequest.getName());
        }
        
        Space space = spaceMapper.toEntity(createSpaceRequest);
        Space savedSpace = spaceRepository.save(space);
        return spaceMapper.toDto(savedSpace);
    }
    
    @Transactional
    public SpaceDto updateSpace(Long id, CreateSpaceRequest updateSpaceRequest) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new SpaceNotFoundException("Space not found with id: " + id));
        
        if (!space.isActive()) {
            throw new SpaceNotFoundException("Space not found with id: " + id);
        }
        
        // Check if name is being changed and if it already exists
        if (!space.getName().equals(updateSpaceRequest.getName()) && 
                spaceRepository.existsByNameAndActiveTrue(updateSpaceRequest.getName())) {
            throw new SpaceAlreadyExistsException("Space already exists with name: " + updateSpaceRequest.getName());
        }
        
        spaceMapper.updateSpaceFromDto(updateSpaceRequest, space);
        Space updatedSpace = spaceRepository.save(space);
        return spaceMapper.toDto(updatedSpace);
    }
    
    @Transactional
    public void deleteSpace(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new SpaceNotFoundException("Space not found with id: " + id));
        
        if (!space.isActive()) {
            throw new SpaceNotFoundException("Space not found with id: " + id);
        }
        
        // Soft delete
        space.setActive(false);
        spaceRepository.save(space);
    }
}

