package com.userapp.spring_aston.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userapp.dto.UserEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "user-events";

    public void sendUserCreatedEvent(String email, String name, Long userId) {
        UserEventDTO event = UserEventDTO.builder()
                .eventType("CREATED")
                .email(email)
                .name(name)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();
        sendEvent(event);
    }

    public void sendUserDeletedEvent(String email, String name, Long userId) {
        UserEventDTO event = UserEventDTO.builder()
                .eventType("DELETED")
                .email(email)
                .name(name)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();
        sendEvent(event);
    }

    private void sendEvent(UserEventDTO event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(TOPIC, event.getEmail(), message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ Event sent successfully: {} to email: {}",
                            event.getEventType(), event.getEmail());
                } else {
                    log.error("❌ Failed to send event: {}", ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error serializing event: {}", e.getMessage(), e);
        }
    }
}