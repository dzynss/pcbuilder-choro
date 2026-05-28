package com.pcbuilder.ms_usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUsuariosApplication.class, args);
	}

}
// http://localhost:8083/api/usuarios
// http://localhost:8083/api/usuarios/login (login interno para administradores)