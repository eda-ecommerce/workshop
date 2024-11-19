package com.eda.shippingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.eda.shippingService")
@EnableKafka
@EntityScan("com.eda.shippingService.domain.entity")
public class ShippingServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceApplication.class, args);
	}
}
