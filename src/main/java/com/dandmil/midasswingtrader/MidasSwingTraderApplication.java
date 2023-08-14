package com.dandmil.midasswingtrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportResource("classpath:integration-config.xml")
public class MidasSwingTraderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MidasSwingTraderApplication.class, args);
	}

}
