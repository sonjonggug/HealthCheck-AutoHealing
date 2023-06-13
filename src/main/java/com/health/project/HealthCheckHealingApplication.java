package com.health.project;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.health.project.service.CheckServerProcess;
import com.health.project.service.CheckServerUsed;
import com.health.project.service.CsvManagement;
import com.health.project.service.SendService;
import com.health.project.utill.Constans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class HealthCheckHealingApplication {
	
	private final CheckServerProcess checkServerProcess ;
	private final CheckServerUsed checkserServerUsed;
	private final CsvManagement csvManagement;
	private final SendService sendService;	
				
	@Value("${csv.path}")
	 String csvPath;
		
	
	public static void main(String[] args) {
		SpringApplication.run(HealthCheckHealingApplication.class, args);
	}

	
	/**
	 * 프로세스 체크
	 */
	@Scheduled(fixedRate = 20000) // 테스트용 30초 , 600000 -> 10분 
    public void checkServerProcess() {        	       				
		try {
				
				/**
				 * 엑셀에서 행은 숫자 열은 알파벳
				 * list.get(0) <- 첫번째 행
				 * list.get(0).get(1) <- 첫번째행에 B열 내용
				 * A:프로세스 네임 , B:경로 , C:실행 커맨드 , D:상태유무
				 */
				ArrayList<List<String>> list = csvManagement.readCSV();	
				
				ArrayList<List<String>> result = checkServerProcess.serverConnect(list);
				
				List<String> restartYN = new ArrayList<String>();
				
				for(int i = 0; i < result.size(); i++) { // result 키 값만큼 반복
					
					if(result.get(i).get(3).equals("Y")) { 			
						log.info(result.get(i).get(0) + Constans.SUCCESS);
						restartYN = result.get(i);	
					}else {
						 restartYN = checkServerProcess.restart(result.get(i));						
						if(restartYN.get(3).equals("Y")) {
							sendService.SendRestartSuccess(restartYN);
							log.info(restartYN.get(0)+ Constans.RESTART_SUCESS);
						}else {
							sendService.SendRestartFail(restartYN);
							log.info(restartYN.get(0) + Constans.RESTART_FAIL);	
						}					
					}	
					result.set(i, restartYN);
				} // for 문 끝 
				csvManagement.writeCSV(result);
					} catch (Exception e) {
					  e.printStackTrace();
					  log.info("Exception Error -----------------> ");
					}
    }
	
	/**
	 * 서버 사용량 체크
	 */
//	@Scheduled(fixedRate = 10000) // 테스트용 30초 , 600000 -> 10분
//	@Scheduled(cron="0/10 * * * * *")
    public void checkServerUsed() {   
		try {												
			boolean result = checkserServerUsed.checkRamUsed();	
			if(result) {
				log.info("성공");
			}else {
				log.info("실패");
			}
				} catch (Exception e) {
				  e.printStackTrace();
				  log.info("Exception Error -----------------> ");
				}
    }	
	
	

	
				
}
