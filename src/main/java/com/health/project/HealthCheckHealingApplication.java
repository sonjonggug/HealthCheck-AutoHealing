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
	
	public static int one_seq = 1 ;
	public static int two_seq = 1 ;
	public static int three_seq = 1 ;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HealthCheckHealingApplication.class, args);
		

	}
	
	
	@Scheduled(fixedRate = 300000) // 테스트용 30초 , 600000 -> 10분 
    public void checkServerProcess() {        	       				
		try {
				// 프로세스 떠 있는지 확인	
											
				
				HashMap<String, Boolean> result = watchDogService.serverConnect();
												
				for(String proccessName : result.keySet()) {
				
					if(result.get(proccessName)) {			
						log.info(proccessName + Constans.SUCCESS);
					}else {
						Boolean restartYN = watchDogService.restart(proccessName);
						
						if(restartYN) {
							sendService.SendRestartSuccess(one_seq , proccessName);
							log.info(proccessName+ Constans.RESTART_SUCESS);
						}else {
							sendService.SendRestartFail(one_seq , proccessName);
							log.info(proccessName + Constans.RESTART_FAIL);	
						}
					}
					
				
				} // for 문 끝 
			
					} catch (Exception e) {
					  e.printStackTrace();
					  log.info("Exception Error -----------------> ");
					}
    }
				
}
