package com.pcbuilder.ms_resenas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de springdoc-openapi para que Swagger UI muestre el botón "Authorize" con Bearer JWT,
 * acorde a la autenticación exigida por {@code SecurityConfig}.
 */
@Configuration
public class OpenApiConfig {

    /** Declara el esquema de seguridad "bearerAuth" (JWT) y lo aplica globalmente a la documentación OpenAPI. */
    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
            .components(new Components().addSecuritySchemes(schemeName,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
