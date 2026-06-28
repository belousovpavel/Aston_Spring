package com.userapp.spring_aston.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userapp.spring_aston.dto.UserRequestDTO;
import com.userapp.spring_aston.dto.UserResponseDTO;
import com.userapp.spring_aston.exception.ResourceNotFoundException;
import com.userapp.spring_aston.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequestDTO validRequest;
    private UserResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        validRequest = UserRequestDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();

        validResponse = UserResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.age", is(30)));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        UserRequestDTO invalidRequest = UserRequestDTO.builder()
                .name("") // Empty name
                .email("invalid-email") // Invalid email
                .age(-1) // Invalid age
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors", hasKey("name")))
                .andExpect(jsonPath("$.validationErrors", hasKey("email")))
                .andExpect(jsonPath("$.validationErrors", hasKey("age")));

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should get user by id")
    void shouldGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Should return 404 when user not found")
    void shouldReturnNotFoundWhenUserNotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User not found with id: 999"));

        mockMvc.perform(get("/api/v1/users/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found with id: 999")));
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() throws Exception {
        UserResponseDTO user2 = UserResponseDTO.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.getAllUsers()).thenReturn(Arrays.asList(validResponse, user2));

        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() throws Exception {
        UserResponseDTO updatedResponse = UserResponseDTO.builder()
                .id(1L)
                .name("John Updated")
                .email("john.updated@example.com")
                .age(31)
                .createdAt(validResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        UserRequestDTO updateRequest = UserRequestDTO.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .age(31)
                .build();

        when(userService.updateUser(eq(1L), any(UserRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.age", is(31)));

        verify(userService, times(1)).updateUser(eq(1L), any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}