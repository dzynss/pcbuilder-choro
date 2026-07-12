package com.pcbuilder.ms_componentes.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de springdoc-openapi: agrega el esquema de seguridad "bearerAuth"
 * (JWT) a la documentación Swagger generada, para poder autorizar las requests
 * de prueba desde swagger-ui.html con el header Authorization: Bearer.
 */
@Configuration
public class OpenApiConfig {

    /** Declara el esquema de autenticación Bearer/JWT usado por todos los endpoints en la doc Swagger. */
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
