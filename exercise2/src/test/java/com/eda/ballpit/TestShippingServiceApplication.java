package com.eda.ballpit;

import com.eda.shippingService.ShippingServiceApplication;
import org.springframework.boot.SpringApplication;

public class TestShippingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShippingServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
