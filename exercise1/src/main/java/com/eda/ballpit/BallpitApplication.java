package com.eda.ballpit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.eda.ballpit")
@EnableKafka
@EntityScan("com.eda.ballpit.domain.entity")
public class BallpitApplication {
	public static void main(String[] args) {
		SpringApplication.run(BallpitApplication.class, args);
	}
}
