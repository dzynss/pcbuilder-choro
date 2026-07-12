package com.pcbuilder.ms_login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada del microservicio ms_login (puerto 8086).
 * Habilita clientes Feign ({@link com.pcbuilder.ms_login.client.UsuarioClient}) para
 * validar credenciales delegando en ms-usuarios (puerto 8083), ya que este servicio
 * no mantiene tabla propia de usuarios.
 */
@SpringBootApplication
@EnableFeignClients
public class MsLoginApplication {

	/** Arranca el contexto de Spring Boot. */
	public static void main(String[] args) {
		SpringApplication.run(MsLoginApplication.class, args);
	}

}
// http://localhost:8086/api/auth/login (POST)