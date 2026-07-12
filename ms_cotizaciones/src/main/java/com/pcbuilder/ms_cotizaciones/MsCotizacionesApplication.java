package com.pcbuilder.ms_cotizaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada del microservicio de cotizaciones (puerto 8087).
 * {@code @EnableFeignClients} activa los clientes Feign ({@code UsuarioClient},
 * {@code ComponenteClient}) usados para validar usuario/componente y obtener el
 * precio real desde ms-usuarios y ms-componentes.
 */
@SpringBootApplication
@EnableFeignClients
public class MsCotizacionesApplication {

	/** Arranca el contexto Spring Boot de este microservicio. */
	public static void main(String[] args) {
		SpringApplication.run(MsCotizacionesApplication.class, args);
	}

}
// http://localhost:8087/api/cotizaciones