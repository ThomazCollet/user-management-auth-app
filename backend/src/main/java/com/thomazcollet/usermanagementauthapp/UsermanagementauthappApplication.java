package com.thomazcollet.usermanagementauthapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UsermanagementauthappApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsermanagementauthappApplication.class, args);
	}

}
