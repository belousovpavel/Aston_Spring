package com.userapp.spring_aston.service;

import com.userapp.spring_aston.dto.UserRequestDTO;
import com.userapp.spring_aston.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO request);
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserRequestDTO request);
    void deleteUser(Long id);
    List<UserResponseDTO> searchUsersByName(String name);
    List<UserResponseDTO> getUsersByAgeRange(Integer minAge, Integer maxAge);
    boolean existsByEmail(String email);
}
