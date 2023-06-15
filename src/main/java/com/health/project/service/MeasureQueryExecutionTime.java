package com.health.project.service;

import org.springframework.stereotype.Service;

import com.health.project.dao.DbCheckDao;
import com.health.project.utill.Constans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MeasureQueryExecutionTime {

	/**
	 * 쿼리 실행 시간 체크
	 * @param dbCheckDao
	 * @param query
	 * @return true : 쿼리 실행 정상 / false : 쿼리 실행 시간 지연 or 문제 발생
	 */
	public boolean measureQueryTime(DbCheckDao dbCheckDao, String query) {
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
            return false;
        }else {
        	return true;
        }
	}		
}
