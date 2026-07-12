package com.pcbuilder.ms_usuarios.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de springdoc-openapi: agrega el esquema de seguridad Bearer JWT a la documentación Swagger UI del servicio. */
@Configuration
public class OpenApiConfig {

    /** Define el bean OpenAPI con el esquema "bearerAuth" (Bearer JWT) requerido por defecto en todas las operaciones documentadas. */
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
