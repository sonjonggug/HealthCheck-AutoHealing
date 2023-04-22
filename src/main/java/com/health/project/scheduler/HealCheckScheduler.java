package com.health.project.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.health.project.service.HealthCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class HealCheckScheduler {

	private final HealthCheckService healthCheckService;
	
	
	 @Scheduled(cron="0/10 * * * * *")
	public void ktServer() {
		
		try {
			boolean rr = healthCheckService.serverConnect();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
}
