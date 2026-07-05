package com.notification.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.dto.UserEventDTO;
import com.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consumeUserEvent(String message) {
        try {
            log.info("Received Kafka message: {}", message);

            UserEventDTO event = objectMapper.readValue(message, UserEventDTO.class);
            log.info("Processing event: {} for user: {}", event.getEventType(), event.getEmail());

            switch (event.getEventType()) {
                case "CREATED":
                    emailService.sendAccountCreatedEmail(event.getEmail(), event.getName());
                    break;
                case "DELETED":
                    emailService.sendAccountDeletedEmail(event.getEmail(), event.getName());
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }
}
