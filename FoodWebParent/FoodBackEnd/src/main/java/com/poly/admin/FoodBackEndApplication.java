package com.poly.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.poly.common.entity", "com.poly.admin.repository"})
public class FoodBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodBackEndApplication.class, args);
	}

}
