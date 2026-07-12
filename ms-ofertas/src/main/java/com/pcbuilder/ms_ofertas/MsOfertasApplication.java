package com.pcbuilder.ms_ofertas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio ms-ofertas (puerto 9091), cupones de descuento.
 * Servicio autocontenido: no depende de otros microservicios (sin carpeta client/) ni es consumido
 * vía Feign por otro servicio actualmente.
 */
@SpringBootApplication
public class MsOfertasApplication {

	/** Arranca el contexto de Spring Boot de este servicio. */
	public static void main(String[] args) {
		SpringApplication.run(MsOfertasApplication.class, args);
	}

}
// http://localhost:9091/swagger-ui/index.html