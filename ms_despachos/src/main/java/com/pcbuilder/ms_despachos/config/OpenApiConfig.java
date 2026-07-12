package com.pcbuilder.ms_despachos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de springdoc-openapi para exponer el esquema de seguridad "bearerAuth" (JWT) en Swagger UI. */
@Configuration
public class OpenApiConfig {

    /** Declara el esquema Bearer/JWT para que Swagger UI permita probar los endpoints protegidos con un token. */
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
