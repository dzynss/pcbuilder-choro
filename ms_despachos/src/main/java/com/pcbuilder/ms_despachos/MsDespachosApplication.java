package com.pcbuilder.ms_despachos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsDespachosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDespachosApplication.class, args);
	}

}
// http://localhost:9093/swagger-ui/index.html