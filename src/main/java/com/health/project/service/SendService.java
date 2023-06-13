package com.health.project.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.health.project.utill.Constans;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendService {
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
	
	
	
	
	public boolean SendRestartSuccess(List<String> proccessStatus) {
											
			log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + proccessStatus.get(0) + " " + Constans.RESTART_SUCESS );
				
		return true;
	}
	
	public boolean SendRestartFail(List<String> proccessStatus) {
				
		int errCheckSum = Integer.parseInt(proccessStatus.get(4)); 
		
			if (errCheckSum % 5 == 0) { // 5분마다 SMS 발송
				log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + proccessStatus.get(0) + " " + Constans.RESTART_FAIL + " (경과시간 " + errCheckSum + " 분)");		    
			} 
						
			log.info("-------> " + ip + ":" + port + " 에 " + proccessStatus.get(0) + " " + Constans.RESTART_FAIL);	
		
			
		return true;
	}

}
