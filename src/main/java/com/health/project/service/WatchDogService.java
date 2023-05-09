package com.health.project.service;

import java.io.InputStream;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WatchDogService {
	
	
	@Value("${cloud.server.id}")
	 String id;
	
	@Value("${cloud.server.pw}")
	 String pw;
	
	@Value("${cloud.server.ip}")
	 String ip;
	
	@Value("${cloud.server.port}")
	 int port;

	
public HashMap<String, Boolean> serverConnect() throws Exception{
		
		log.info("서버 프로세스 확인 중...");
		
		HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();								
		
		// JSch 라이브러리로 SSH 세션을 생성
        JSch jsch = new JSch();
        Session session = jsch.getSession(id, ip , port);
        session.setPassword(pw);
        
        // 호스트 키 확인을 생략.
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        
        // SSH 연결을 시도.
        session.connect();  
        
        // SSH 채널을 열고, 실행할 명령어를 설정.
        Channel channel = session.openChannel("exec");  // 채널접속  exec = 명령을 요청하기 위함 
        ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand("ps -ef | grep java"); // 내가 실행시킬 명령어를 입력
      
        // 명령어의 실행 결과를 받을 준비.
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err); 
        
        // 명령어를 실행하고, 결과를 받아옴.
        channel.connect();  //실행
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, i));
                if (i < 0) break;
            }
            // 명령어의 실행이 끝나면, 실행 결과를 분석
            if (channel.isClosed()) {            	            	
            	                
                boolean pointer = outputBuffer.toString().contains("-DSERVICE_NAME=pointerd");
                boolean pointer_example = outputBuffer.toString().contains("-DSERVICE_NAME=pointer_exampled");
                boolean poodle = outputBuffer.toString().contains("-DSERVICE_NAME=poodle");
                
                hashMap.put("pointer", pointer);
                hashMap.put("pointer_example", pointer_example);
                hashMap.put("poodle", poodle);
                                                
                log.info("서버 명령어 결과 ------> " + outputBuffer.toString());                               
                                            
                // SSH 연결을 종료하고, 결과값을 반환
                channel.disconnect();
                session.disconnect();
                
                // 서버가 꺼져있을 시 재기동 
//                if(!bool) {
//                	result = this.ifProcDown();
//                }                            
                return hashMap;                
            }      
        }  
			
    }
			
	/**
	 * 프로세스가 다운됬을 시 재 실행
	 * @return
	 * @throws Exception
	 */
	public boolean ifProcDown(String serverName) throws Exception{
		
		log.info("프로세스 다운 재기동... "+ serverName);
				
//		
//		try {
//							
//		// JSch 라이브러리로 SSH 세션을 생성
//        JSch jsch = new JSch();
//        Session session = jsch.getSession(id, ip , port);
//        session.setPassword(pw);
//        
//        // 호스트 키 확인을 생략.
//        java.util.Properties config = new java.util.Properties();
//        config.put("StrictHostKeyChecking", "no");
//        session.setConfig(config);
//        
//        // SSH 연결을 시도.
//        session.connect();  
//        
//        // SSH 채널을 열고, 실행할 명령어를 설정.
//        Channel channel = session.openChannel("exec");  // 채널접속  exec = 명령을 요청하기 위함 
//        ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
//        channelExec.setPty(true);
//        
//       
//        // 내가 실행시킬 명령어를 입력
//        channelExec.setCommand("API-Server/shell/javaStart.sh");
//        
//        // 명령어의 실행 결과를 받을 준비를 합니다.
//        StringBuilder outputBuffer = new StringBuilder();
//        InputStream in = channel.getInputStream();
//        ((ChannelExec) channel).setErrStream(System.err); 
//        
//        // 명령어를 실행하고, 결과를 받아옴.
//        channel.connect();  //실행
//        byte[] tmp = new byte[1024];
//        while (true) {
//            while (in.available() > 0) {
//                int i = in.read(tmp, 0, 1024);
//                outputBuffer.append(new String(tmp, 0, i));
//                if (i < 0) break;
//            }
//            // 명령어의 실행이 끝나면, 실행 결과를 분석
//            if (channel.isClosed()) {
//            	            	           	
//              boolean bool = outputBuffer.toString().contains("successfully");     
//                                                        
//               if(bool) {
//               	result = "R";
//               }else {
//               	result = "N";
//               }
//               
//                // SSH 연결을 종료하고, 결과값을 반환
//                channel.disconnect();
//                session.disconnect();
//                return result;
//                
//            }      
//        } 
//			} catch (Exception e) {
//				e.printStackTrace();
//				 result = "N";
//				 return result;			
//			}
		return false;
    }
}

	

