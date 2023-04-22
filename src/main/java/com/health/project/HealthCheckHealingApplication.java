package com.health.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.health.project.service.HealthCheckService;

@SpringBootApplication
public class HealthCheckHealingApplication {
	
	
	
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HealthCheckHealingApplication.class, args);
		
		Startt();
	}
	
	
	
	public static boolean Startt() throws Exception {
		HealthCheckService healthCheckService = new HealthCheckService();
		boolean rr = healthCheckService.serverConnect();
		
		return rr;
	}
}
