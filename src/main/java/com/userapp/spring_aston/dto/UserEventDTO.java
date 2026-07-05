package com.userapp.spring_aston.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO {
    private String eventType; // "CREATED" or "DELETED"
    private String email;
    private String name;
    private Long userId;
    private LocalDateTime timestamp;
}