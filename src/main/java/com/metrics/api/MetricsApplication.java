package com.metrics.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MetricsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetricsApplication.class, args);
	}

}
