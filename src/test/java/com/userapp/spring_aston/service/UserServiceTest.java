package com.userapp.spring_aston.service;

import com.userapp.spring_aston.dto.UserRequestDTO;
import com.userapp.spring_aston.dto.UserResponseDTO;
import com.userapp.spring_aston.entity.User;
import com.userapp.spring_aston.exception.DuplicateEmailException;
import com.userapp.spring_aston.exception.ResourceNotFoundException;
import com.userapp.spring_aston.mapper.UserMapper;
import com.userapp.spring_aston.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO requestDTO;
    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = UserRequestDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void shouldThrowExceptionWhenDuplicateEmail() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by id")
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        UserRequestDTO updateRequest = UserRequestDTO.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .age(31)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .name("John Updated")
                .email("john.updated@example.com")
                .age(31)
                .build();

        UserResponseDTO updatedResponse = UserResponseDTO.builder()
                .id(1L)
                .name("John Updated")
                .email("john.updated@example.com")
                .age(31)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDTO(updatedUser)).thenReturn(updatedResponse);

        UserResponseDTO result = userService.updateUser(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(result.getAge()).isEqualTo(31);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistent() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should search users by name")
    void shouldSearchUsersByName() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findByNameContainingIgnoreCase("Doe")).thenReturn(users);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        List<UserResponseDTO> result = userService.searchUsersByName("Doe");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");

        verify(userRepository, times(1)).findByNameContainingIgnoreCase("Doe");
    }
}
