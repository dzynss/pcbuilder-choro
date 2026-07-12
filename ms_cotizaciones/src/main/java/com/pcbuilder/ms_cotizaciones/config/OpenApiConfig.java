package com.pcbuilder.ms_cotizaciones.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración de springdoc-openapi: agrega el esquema de seguridad "bearerAuth" a Swagger UI para poder probar endpoints protegidos con JWT. */
@Configuration
public class OpenApiConfig {

    /** Declara el esquema HTTP Bearer/JWT usado por todos los endpoints documentados en Swagger. */
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
