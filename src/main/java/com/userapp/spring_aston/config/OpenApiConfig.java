package com.userapp.spring_aston.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("""
                    REST API for managing users with Kafka integration.
                    
                    ## Features:
                    - Create, Read, Update, Delete users
                    - Search users by name
                    - Filter users by age range
                    - Check email availability
                    - Kafka event publishing for user creation/deletion
                    - HATEOAS support for resource navigation
                    
                    ## Integration:
                    - PostgreSQL for data storage
                    - Kafka for event-driven architecture
                    - Spring Boot 3.1.5
                    """)
                        .contact(new Contact()
                                .name("User Service Team")
                                .email("support@userapp.com")
                                .url("https://github.com/username/user-service"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production Server")
                ))
                .tags(List.of(
                        new Tag().name("User Management").description("Operations for managing users")
                ));
    }
}
