package com.pcbuilder.ms_notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsNotificacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsNotificacionesApplication.class, args);
	}

}
// http://localhost:9094/swagger-ui/index.html