package com.health.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.health.project.utill.Constans;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendService {
	
	@Value("${cloud.server.ip}")
	 String ip;
	
	@Value("${cloud.server.port}")
	 int port;
	
	
	public boolean SendMail(int SendSeq , String serverName) {
		log.info("메일 발송 -------> ");
		
		
		log.info(ip + ":" + port + " 에 " + serverName + " " + Constans.RESTART_FAIL);
		
		return true;
	}

}
