package com.health.project;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.health.project.dao.DbCheckDao;
import com.health.project.service.CheckServerProcess;
import com.health.project.service.CheckServerUsed;
import com.health.project.service.CsvManagement;
import com.health.project.service.MeasureQueryExecutionTime;
import com.health.project.service.SendService;
import com.health.project.utill.Constans;
import com.health.project.utill.DbConnectionFactory;

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
	private final MeasureQueryExecutionTime measureQueryExecutionTime;	
				
	@Value("${csv.path}")
	 String csvPath;
		
	@Value("${target.java.use.yn}")
	 String javaUseYn;

	@Value("${target.sys.use.yn}")
	 String sysUseYn;
	
	@Value("${target.db.use.yn}")
	 String dbUseYn;		
	
	public static void main(String[] args) {
		SpringApplication.run(HealthCheckHealingApplication.class, args);
	}

	
	/**
	 * 자바 프로세스 체크
	 */
	@Scheduled(fixedRate = 20000) // 테스트용 30초 , 600000 -> 10분 
    public void checkServerProcess() {
		if(javaUseYn.equals(Constans.USE_YN_Y)) {
			
			try {				
				/**
				 * 엑셀에서 행은 숫자 열은 알파벳
				 * list.get(0) <- 첫번째 행
				 * list.get(0).get(1) <- 첫번째행에 B열 내용
				 * A:프로세스 네임 , B:경로 , C:실행 커맨드 , D:상태유무 , E:장애횟수
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
					result.set(i, restartYN); // 변경된 리스트 값을 set 
				} // for 문 끝 
				csvManagement.writeCSV(result); // 변경된 리스트 값 csv에 적용
					} catch (Exception e) {
					  e.printStackTrace();
					  log.info("Exception Error -----------------> ");
					}
		} else {		
			log.info("자바프로세스 모니터링 사용 안함");
		}
    }
	
	/**
	 * 서버 사용량 체크
	 */
	@Scheduled(fixedRate = 20000) // 테스트용 30초 , 600000 -> 10분
//	@Scheduled(cron="0/10 * * * * *")
    public void checkServerUsed() {
      if(sysUseYn.equals(Constans.USE_YN_Y)) {
		try {												
			boolean result = checkserServerUsed.checkServerUsed();	
			if(result) {
				log.info("성공");
			}else {
				log.info("실패");
			}
				} catch (Exception e) {
				  e.printStackTrace();
				  log.info("Exception Error -----------------> ");
				}
      	}else {
      		log.info("서버 사용량 모니터링 사용안함");
      	}
    }		
	
	/**
	 * DB 체크
	 */	
	@Scheduled(fixedDelay = 300000) // 테스트용 30초 , 600000 -> 10분 
	public void checkDataBase() {		
		if(dbUseYn.equals(Constans.USE_YN_Y)) {
			// DB 연결
			DbCheckDao dbCheckDao = new DbCheckDao(DbConnectionFactory.getChkDbSqlSessionFactory());
			try {
				// DB에  INSERT
		//		measureQueryExecutionTime.measureQueryTime(dbCheckDao, Constans.dbInsert);
		
				// DB에  UPDATE
		//		measureQueryExecutionTime.measureQueryTime(dbCheckDao, Constans.dbUpdate);
		
				// DB에  DELETE
				measureQueryExecutionTime.measureQueryTime(dbCheckDao, Constans.dbDelete);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception Error -----------------> ");
		    }
		}else {
      		log.info("DB 체크 모니터링 사용안함");
      	}
	}
	
	/**
	 * 쿼리 실행 시간 체크
	 */
	public void measureQueryExecutionTime(DbCheckDao dbCheckDao, String query) {
        // 쿼리 실행 시간 측정 시작
        long startTime = System.currentTimeMillis();
        
		switch (query) {
		case Constans.dbInsert:
			dbCheckDao.dbInsert();
			break;
		case Constans.dbUpdate:
			dbCheckDao.dbUpdate();
			break;
		case Constans.dbDelete:
			dbCheckDao.dbDelete();
			break;
		}
		
		// 쿼리 실행 시간 측정 종료
        long endTime = System.currentTimeMillis();
        
        // 실행 시간 계산
        long elapsedTime = endTime - startTime;
        
        if (elapsedTime >= Constans.elapsedTime) {
            System.out.println("쿼리 실행시간 10초 이상입니다. (" + elapsedTime + " milliseconds)");
            // 추가 작업 - email 발송 or SMS 처리
        } 
	}						
}
