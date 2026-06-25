package com.pcbuilder.ms_resenas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsResenasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsResenasApplication.class, args);
	}

}
// http://localhost:8084/api/resenas