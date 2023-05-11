package com.health.project.service;


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
	
	
	
	
	public boolean SendRestartSuccess(int SendSeq , String serverName) {
		
		if(SendSeq == 1) {
			log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + serverName + " " + Constans.RESTART_SUCESS);
		}else {
			log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + serverName + " " + Constans.RESTART_FAIL + " (경과시간 " + SendSeq + "0분)");
		}
		
		return true;
	}
	
	public boolean SendRestartFail(int SendSeq , String serverName) {
		
		if(SendSeq == 1) {
			log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + serverName + " " + Constans.RESTART_SUCESS );
		}else {
			log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + serverName + " " + Constans.RESTART_FAIL + " (경과시간 " + SendSeq + "0분)");
		}
						
		
		return true;
	}

}
