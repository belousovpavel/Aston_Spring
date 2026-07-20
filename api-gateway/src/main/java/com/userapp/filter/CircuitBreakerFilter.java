package com.userapp.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CircuitBreakerFilter extends AbstractGatewayFilterFactory<CircuitBreakerFilter.Config> {

    public CircuitBreakerFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Circuit Breaker Filter: {}", exchange.getRequest().getPath());

            return chain.filter(exchange)
                    .doOnError(error -> {
                        log.error("Circuit breaker triggered: {}", error.getMessage());
                        exchange.getResponse().getHeaders().add("X-CircuitBreaker-Status", "OPEN");
                    });
        };
    }

    public static class Config {
        private String name;
        private String fallbackUri;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFallbackUri() {
            return fallbackUri;
        }

        public void setFallbackUri(String fallbackUri) {
            this.fallbackUri = fallbackUri;
        }
    }
}
