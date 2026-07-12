package com.pcbuilder.ms_inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio ms-inventario (puerto 9090), gestión de stock de piezas.
 * Servicio autocontenido: no depende de otros microservicios (sin carpeta client/) ni es consumido
 * vía Feign por otro servicio actualmente.
 */
@SpringBootApplication
public class MsInventarioApplication {

	/** Arranca el contexto de Spring Boot de este servicio. */
	public static void main(String[] args) {
		SpringApplication.run(MsInventarioApplication.class, args);
	}

}
// http://localhost:9090/swagger-ui/index.html