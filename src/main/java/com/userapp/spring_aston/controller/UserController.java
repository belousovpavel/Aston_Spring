package com.userapp.spring_aston.controller;

import com.userapp.spring_aston.dto.UserRequestDTO;
import com.userapp.spring_aston.dto.UserResponseDTO;
import com.userapp.spring_aston.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("POST /api/v1/users - Create user: {}", request.getEmail());
        UserResponseDTO response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("GET /api/v1/users/{} - Get user by id", id);
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/v1/users - Get all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request) {
        log.info("PUT /api/v1/users/{} - Update user", id);
        UserResponseDTO response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} - Delete user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByName(@RequestParam String name) {
        log.info("GET /api/v1/users/search?name={} - Search users by name", name);
        List<UserResponseDTO> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/age-range")
    public ResponseEntity<List<UserResponseDTO>> getUsersByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        log.info("GET /api/v1/users/age-range?minAge={}&maxAge={} - Get users by age range", minAge, maxAge);
        List<UserResponseDTO> users = userService.getUsersByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        log.info("GET /api/v1/users/check-email?email={} - Check if email exists", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}