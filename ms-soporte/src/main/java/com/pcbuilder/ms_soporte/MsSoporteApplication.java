package com.pcbuilder.ms_soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSoporteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSoporteApplication.class, args);
	}

}
// http://localhost:9092/swagger-ui/index.html