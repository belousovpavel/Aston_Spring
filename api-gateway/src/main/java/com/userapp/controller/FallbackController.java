package com.userapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("status", "FALLBACK");
        fallback.put("message", "User service is currently unavailable. Please try again later.");
        fallback.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallback);
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("status", "FALLBACK");
        fallback.put("message", "Notification service is currently unavailable. Please try again later.");
        fallback.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallback);
    }
}
