package com.pcbuilder.ms_despachos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase de arranque del microservicio ms_despachos (puerto 9093).
 * @EnableFeignClients habilita el UsuarioClient que consume ms-usuarios (puerto 8083).
 */
@SpringBootApplication
@EnableFeignClients
public class MsDespachosApplication {

	/** Punto de entrada estándar de Spring Boot. */
	public static void main(String[] args) {
		SpringApplication.run(MsDespachosApplication.class, args);
	}

}
// http://localhost:9093/swagger-ui/index.html