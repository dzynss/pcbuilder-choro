package com.pcbuilder.ms_soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada del microservicio ms-soporte (puerto 9092). {@code @EnableFeignClients}
 * habilita los clients hacia ms-usuarios y ms-componentes usados para validar tickets.
 */
@SpringBootApplication
@EnableFeignClients
public class MsSoporteApplication {

	/** Arranca el contexto Spring Boot de este servicio. */
	public static void main(String[] args) {
		SpringApplication.run(MsSoporteApplication.class, args);
	}

}
// http://localhost:9092/swagger-ui/index.html