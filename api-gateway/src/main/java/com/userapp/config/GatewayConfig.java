package com.userapp.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service routes
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/users"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(java.util.List.of(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR))
                                        .setMethods(java.util.List.of(org.springframework.http.HttpMethod.GET))
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .addRequestHeader("X-Gateway-Request", "true")
                                .addResponseHeader("X-Gateway-Response", "true"))
                        .uri("lb://USER-SERVICE"))

                // Notification Service routes
                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("notificationServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/notifications"))
                                .addRequestHeader("X-Gateway-Request", "true")
                                .addResponseHeader("X-Gateway-Response", "true"))
                        .uri("lb://NOTIFICATION-SERVICE"))

                // Swagger aggregation
                .route("swagger-aggregation", r -> r
                        .path("/swagger-ui/**", "/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/api-docs/(?<path>.*)", "/${path}/api-docs"))
                        .uri("lb://USER-SERVICE"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
    }
}