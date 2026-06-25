package com.pcbuilder.ms_login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsLoginApplication.class, args);
	}

}
// http://localhost:8086/api/auth/login (POST) 