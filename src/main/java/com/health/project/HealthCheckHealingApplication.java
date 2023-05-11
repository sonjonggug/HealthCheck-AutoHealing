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
								
				String proccess = null;
				String serviceName = null;
				
				HashMap<String, Object> result = watchDogService.serverConnect();
				
				for(int i = 0 ; result.size()/2 > i; i++) {
					serviceName = "serviceName_"+i;
					proccess = "process_"+i;
				
				if((boolean) result.get(proccess)) {			
					one_seq = 1;
					log.info(result.get(serviceName) + Constans.SUCCESS);
				// 재기동 실패 시 계속 재기동 시키면 안되기에 처음 한번 , 1시간 마다 재기동
				}else if(one_seq == 1 || one_seq % 6 == 0){
					Boolean restartYN = watchDogService.restart((String) result.get(serviceName));
					if(restartYN) {				
						one_seq = 1;
						sendService.SendRestartSuccess(one_seq , (String) result.get(serviceName));
						log.info((String) result.get(serviceName)+ Constans.RESTART_SUCESS);
					} else {
						one_seq += 1;
						sendService.SendRestartFail(one_seq , (String) result.get(serviceName));
						log.info((String) result.get(serviceName)+ Constans.RESTART_FAIL);				
					}
				} else {
					one_seq += 1;
					log.info("pomeranian_sso "+ Constans.FAIL);
					}
				
				} // for 문 끝 
			
					} catch (Exception e) {
					  e.printStackTrace();
					  log.info("Exception Error -----------------> ");
					}
    }
				
}
