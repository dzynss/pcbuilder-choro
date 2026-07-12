package com.pcbuilder.ms_notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada del microservicio ms_notificaciones (puerto 9094).
 * {@code @EnableFeignClients} habilita el {@code UsuarioClient} que consume ms-usuarios (puerto 8083).
 */
@SpringBootApplication
@EnableFeignClients
public class MsNotificacionesApplication {

	/** Arranca el contexto de Spring Boot para este microservicio. */
	public static void main(String[] args) {
		SpringApplication.run(MsNotificacionesApplication.class, args);
	}

}
// http://localhost:9094/swagger-ui/index.html