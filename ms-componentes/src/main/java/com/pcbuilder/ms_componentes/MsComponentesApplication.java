package com.pcbuilder.ms_componentes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase de arranque del microservicio ms-componentes (catálogo de piezas de PC
 * y sus categorías), expuesto en el puerto 8085. No depende de otros
 * microservicios, pero es consumido vía Feign por ms-resenas, ms_cotizaciones
 * y ms-soporte.
 */
@SpringBootApplication
public class MsComponentesApplication {

	/** Punto de entrada estándar de Spring Boot: inicializa el contexto de la aplicación. */
	public static void main(String[] args) {
		SpringApplication.run(MsComponentesApplication.class, args);
	}

}
//http://localhost:8085/api/componentes