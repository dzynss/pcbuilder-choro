package com.pcbuilder.ms_cotizaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCotizacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCotizacionesApplication.class, args);
	}

}
// http://localhost:8087/api/cotizaciones