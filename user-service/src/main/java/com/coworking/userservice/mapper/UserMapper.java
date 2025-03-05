package com.coworking.userservice.mapper;

import com.coworking.userservice.dto.CreateUserRequest;
import com.coworking.userservice.dto.UserDto;
import com.coworking.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDto toDto(User user);
    
    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);
}

