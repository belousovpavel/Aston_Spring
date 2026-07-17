package com.userapp.spring_aston.controller;

import com.userapp.dto.UserRequestDTO;
import com.userapp.dto.UserResponseDTO;
import com.userapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Operations for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request) {
        log.info("POST /api/v1/users - Create user: {}", request.getEmail());
        UserResponseDTO response = userService.createUser(request);

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(UserController.class).getUserById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        response.add(linkTo(methodOn(UserController.class).updateUser(response.getId(), null))
                .withRel("update"));
        response.add(linkTo(methodOn(UserController.class).deleteUser(response.getId()))
                .withRel("delete"));
        response.add(linkTo(methodOn(UserController.class).searchUsersByName(null))
                .withRel("search"));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/v1/users/{} - Get user by id", id);
        UserResponseDTO response = userService.getUserById(id);

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        response.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));
        response.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        response.add(linkTo(methodOn(UserController.class).searchUsersByName(null)).withRel("search"));

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
    })
    public ResponseEntity<CollectionModel<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/v1/users - Get all users");
        List<UserResponseDTO> users = userService.getAllUsers();

        // Добавляем HATEOAS ссылки для каждого пользователя
        users.forEach(user -> {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
            user.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null))
                    .withRel("update"));
            user.add(linkTo(methodOn(UserController.class).deleteUser(user.getId()))
                    .withRel("delete"));
        });

        // Добавляем ссылки для коллекции
        CollectionModel<UserResponseDTO> collectionModel = CollectionModel.of(users);
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        collectionModel.add(linkTo(methodOn(UserController.class).createUser(null))
                .withRel("create"));
        collectionModel.add(linkTo(methodOn(UserController.class).searchUsersByName(null))
                .withRel("search"));

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request) {
        log.info("PUT /api/v1/users/{} - Update user", id);
        UserResponseDTO response = userService.updateUser(id, request);

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        response.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        response.add(linkTo(methodOn(UserController.class).searchUsersByName(null)).withRel("search"));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} - Delete user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name", description = "Searches for users by name (partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
    })
    public ResponseEntity<CollectionModel<UserResponseDTO>> searchUsersByName(
            @Parameter(description = "Name to search for", required = true, example = "John")
            @RequestParam String name) {
        log.info("GET /api/v1/users/search?name={} - Search users by name", name);
        List<UserResponseDTO> users = userService.searchUsersByName(name);

        // Добавляем HATEOAS ссылки для каждого пользователя
        users.forEach(user -> {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
            user.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null))
                    .withRel("update"));
            user.add(linkTo(methodOn(UserController.class).deleteUser(user.getId()))
                    .withRel("delete"));
        });

        CollectionModel<UserResponseDTO> collectionModel = CollectionModel.of(users);
        collectionModel.add(linkTo(methodOn(UserController.class).searchUsersByName(name)).withSelfRel());
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        collectionModel.add(linkTo(methodOn(UserController.class).createUser(null))
                .withRel("create"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/age-range")
    @Operation(summary = "Get users by age range", description = "Retrieves users within a specified age range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
    })
    public ResponseEntity<CollectionModel<UserResponseDTO>> getUsersByAgeRange(
            @Parameter(description = "Minimum age", required = true, example = "20")
            @RequestParam Integer minAge,
            @Parameter(description = "Maximum age", required = true, example = "30")
            @RequestParam Integer maxAge) {
        log.info("GET /api/v1/users/age-range?minAge={}&maxAge={} - Get users by age range", minAge, maxAge);
        List<UserResponseDTO> users = userService.getUsersByAgeRange(minAge, maxAge);

        users.forEach(user -> {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
            user.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null))
                    .withRel("update"));
            user.add(linkTo(methodOn(UserController.class).deleteUser(user.getId()))
                    .withRel("delete"));
        });

        CollectionModel<UserResponseDTO> collectionModel = CollectionModel.of(users);
        collectionModel.add(linkTo(methodOn(UserController.class).getUsersByAgeRange(minAge, maxAge))
                .withSelfRel());
        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        collectionModel.add(linkTo(methodOn(UserController.class).searchUsersByName(null))
                .withRel("search"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check if email exists", description = "Checks if an email is already registered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email check completed")
    })
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email to check", required = true, example = "john@example.com")
            @RequestParam String email) {
        log.info("GET /api/v1/users/check-email?email={} - Check if email exists", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}