package com.health.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.health.project.service.HealthCheckService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class HealthCheckHealingApplication {
	
	@Autowired
	HealthCheckService healthCheckService ;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HealthCheckHealingApplication.class, args);
		

	}
	
	
	@Scheduled(fixedRate = 1800000)  // 30분마다 프로세스 확인
	    public void checkServerProcess() throws Exception {
	        	       

			String result = healthCheckService.serverConnect();
			System.out.println(result);
			if(result.equals("Y")) {
				log.info("프로세스가 정상작동 중입니다.");
			}else if(result.equals("R")) {
				log.info("프로세스가 작동하지 않아 재기동 되었습니다.");
			} else {
				log.info("프로세스가 작동하지 않습니다.");
			}
	    }

}
