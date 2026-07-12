package com.pcbuilder.ms_usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio de usuarios (puerto 8083): expone CRUD de
 * usuarios y verificación de credenciales. No depende de otros microservicios,
 * pero es consumido vía Feign por ms_login, ms_cotizaciones, ms-soporte,
 * ms_despachos y ms_notificaciones.
 */
@SpringBootApplication
public class MsUsuariosApplication {

	/** Arranca el contexto de Spring Boot de este microservicio. */
	public static void main(String[] args) {
		SpringApplication.run(MsUsuariosApplication.class, args);
	}

}
// http://localhost:8083/api/usuarios
// http://localhost:8083/api/usuarios/login (login interno para administradores)