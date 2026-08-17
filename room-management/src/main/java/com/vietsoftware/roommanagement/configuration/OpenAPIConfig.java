package com.vietsoftware.roommanagement.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI / Swagger UI documentation in Room Management Microservice.
 *
 * @author VietSoftware
 * @version 1.0.0
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Configures metadata and information for OpenAPI / Swagger documentation.
     *
     * @return {@link OpenAPI} instance configured with API details
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Room Management Microservice API")
                        .version("1.0.0")
                        .description("RESTful API documentation for Room Management Microservice, providing endpoints to create, update, search, retrieve, and delete rooms.")
                        .contact(new Contact()
                                .name("VietSoftware Support")))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }
}
