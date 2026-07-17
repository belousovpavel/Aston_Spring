package com.userapp.spring_aston.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response object")
public class ErrorResponse {

    @Schema(description = "Timestamp of the error", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error type", example = "Not Found")
    private String error;

    @Schema(description = "Error message", example = "User not found with id: 1")
    private String message;

    @Schema(description = "Request path", example = "/api/v1/users/1")
    private String path;

    @Schema(description = "Validation errors (if any)")
    private Map<String, String> validationErrors;
}
