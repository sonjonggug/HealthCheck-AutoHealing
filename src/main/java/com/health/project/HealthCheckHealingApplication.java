package com.health.project;

import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.health.project.service.SendService;
import com.health.project.service.WatchDogService;
import com.health.project.utill.Constans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class HealthCheckHealingApplication {
	
	private final WatchDogService watchDogService ;	
	private final SendService sendService;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HealthCheckHealingApplication.class, args);
		

	}
	
	
	@Scheduled(fixedRate = 30000) // 테스트용 30초
    public void checkServerProcess() throws Exception {        	       		
		
		try {
			
		HashMap<String, Boolean> result = watchDogService.serverConnect();				
				
		if(result.get("pointer")) {			
			Constans.one_seq = 1;
			log.info("pointer "+ Constans.SUCCESS);
//			재기동 실패 시 계속 재기동 시키면 안되기에 처음 한번 , 1시간되면 한번 더 재기동
		}else if(Constans.one_seq == 1 || Constans.one_seq==6){
			Boolean YN = watchDogService.ifProcDown("pointer");
			if(YN) {				
				Constans.one_seq = 1;
				log.info("pointer "+ Constans.RESTART_SUCESS);
			} else {
				Constans.one_seq += 1;
				sendService.SendMail(Constans.one_seq , "pointer");
				log.info("pointer "+ Constans.RESTART_FAIL);				
			}
		} else {
			Constans.one_seq += 1;
			log.info("pointer "+ Constans.FAIL);
		}
		
		
		if(result.get("pointer_example")) {			
			Constans.two_seq = 1;
			log.info("pointer_example "+ Constans.SUCCESS);
		// 재기동 실패 시 계속 재기동 시키면 안되기에 처음 한번 , 1시간되면 한번 더 재기동
		}else if(Constans.two_seq == 1 || Constans.two_seq==6){
			Boolean YN = watchDogService.ifProcDown("pointer_example");
			if(YN) {				
				Constans.two_seq = 1;
				log.info("pointer_example "+ Constans.RESTART_SUCESS);
			} else {
				Constans.two_seq += 1;
				sendService.SendMail(Constans.two_seq , "pointer_example");
				log.info("pointer_example "+ Constans.RESTART_FAIL);				
			}
		} else {
			Constans.two_seq += 1;
			log.info("pointer_example "+ Constans.FAIL);
		}
		
		
		if(result.get("poodle")) {			
			Constans.three_seq = 1;
			log.info("poodle "+ Constans.SUCCESS);
//			재기동 실패 시 계속 재기동 시키면 안되기에 처음 한번 , 1시간되면 한번 더 재기동 및 SMS 발송
		}else if(Constans.three_seq == 1 || Constans.three_seq == 6){
			Boolean YN = watchDogService.ifProcDown("poodle");
			if(YN) {				
				Constans.three_seq = 1;
				log.info("poodle "+ Constans.RESTART_SUCESS);
			} else {
				Constans.three_seq += 1;
				sendService.SendMail(Constans.three_seq , "poodle");
				log.info("poodle "+ Constans.RESTART_FAIL);				
			}
		} else {
			Constans.three_seq = 1;
			log.info("poodle "+ Constans.FAIL);
		}
	 	
			} catch (Exception e) {
			  e.printStackTrace();
			  log.info("Exception Error -----------------> ");
			}
    }
}



