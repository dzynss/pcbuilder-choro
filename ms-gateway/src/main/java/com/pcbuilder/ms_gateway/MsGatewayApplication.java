package com.pcbuilder.ms_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de Spring Cloud Gateway (puerto 9099): único punto de entrada externo
 * del sistema. Las rutas hacia los 10 microservicios de negocio (por prefijo de path:
 * /api/usuarios/**, /api/resenas/**, /api/componentes/**, /api/auth/**,
 * /api/cotizaciones/**, /api/inventario/**, /api/ofertas/**, /api/soporte/**,
 * /api/despachos/**, /api/notificaciones/**) están declaradas en
 * src/main/resources/application-dev.yml, no en código Java. Antes de enrutar,
 * {@link com.pcbuilder.ms_gateway.filter.JwtValidationGlobalFilter} valida el JWT.
 */
@SpringBootApplication
public class MsGatewayApplication {

    /** Arranca el contexto de Spring Boot / WebFlux del gateway. */
    public static void main(String[] args) {
        SpringApplication.run(MsGatewayApplication.class, args);
    }
}
