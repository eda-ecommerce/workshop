package com.eda.ballpit;

import org.springframework.boot.SpringApplication;

public class TestShippingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(BallpitApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
