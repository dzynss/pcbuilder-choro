package com.pcbuilder.ms_resenas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase de arranque de ms-resenas (puerto 8084).
 * {@code @EnableFeignClients} habilita {@code ComponenteClient}, usado para validar componentes contra ms-componentes.
 */
@SpringBootApplication
@EnableFeignClients
public class MsResenasApplication {

	/** Punto de entrada estándar de Spring Boot. */
	public static void main(String[] args) {
		SpringApplication.run(MsResenasApplication.class, args);
	}

}
// http://localhost:8084/api/resenas