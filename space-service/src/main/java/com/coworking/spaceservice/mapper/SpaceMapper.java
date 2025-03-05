package com.coworking.spaceservice.mapper;

import com.coworking.spaceservice.dto.CreateSpaceRequest;
import com.coworking.spaceservice.dto.SpaceDto;
import com.coworking.spaceservice.model.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    
    SpaceDto toDto(Space space);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Space toEntity(CreateSpaceRequest createSpaceRequest);
    
    @Mapping(target = "id", ignore = true)
    void updateSpaceFromDto(CreateSpaceRequest updateSpaceRequest, @MappingTarget Space space);
}

