package com.coworking.userservice.service;

import com.coworking.userservice.dto.CreateUserRequest;
import com.coworking.userservice.dto.UserDto;
import com.coworking.userservice.exception.EmailAlreadyExistsException;
import com.coworking.userservice.exception.UserNotFoundException;
import com.coworking.userservice.mapper.UserMapper;
import com.coworking.userservice.model.User;
import com.coworking.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDto createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + createUserRequest.getEmail());
        }
        
        User user = userMapper.toEntity(createUserRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    @Transactional
    public UserDto updateUser(Long id, CreateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(updateUserRequest.getEmail()) && 
                userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + updateUserRequest.getEmail());
        }
        
        user.setEmail(updateUserRequest.getEmail());
        user.setName(updateUserRequest.getName());
        
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}

