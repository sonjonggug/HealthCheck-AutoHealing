package com.health.project.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	
	
	@Value("${dev.server.id}")
	 String id;
	
	@Value("${dev.server.pw}")
	 String pw;
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
	
	@Value("${process.name}")
	String processName ;

	
public HashMap<String, Boolean> serverConnect() throws Exception{
		
		log.info("서버 프로세스 확인 중..... " + processName);
		
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
        channelExec.setCommand("ps -ef | grep java"); // 실행시킬 명령어를 입력
      
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
            	
            	// processName 을 | 기준으로 잘라서 리스트에 넣기
            	List<String> list = Arrays.asList(processName.split("\\|"));
            	HashMap<String, Boolean> map = new HashMap<String, Boolean>();
            	String serviceName = null;
            	String proccessYN = null;
            	
            	// 리스트 사이즈만큼 돌면서 map에 담기
            	for(int i = 0 ; list.size() > i; i++) {
            		proccessYN = list.get(i);

            		map.put(proccessYN , outputBuffer.toString().contains("-DSERVICE_NAME="+list.get(i)));
            		log.info(list.get(i)+ " 동작 상태 --------> " + map.get(proccessYN));            	
            	}
            	            	            	            	                            
                                            
                // SSH 연결을 종료하고, 결과값을 반환
                channel.disconnect();
                session.disconnect();
                                             
                return map;                
            }      
        }  
			
    }
			
	/**
	 * 프로세스가 다운됬을 시 재 실행
	 * @return
	 * @throws Exception
	 */
	public boolean restart(String serverName) throws Exception{
		
		log.info("프로세스 다운 재기동... "+ serverName);
		boolean bool = false ;						
							
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
        
        // 실행시킬 명령어를 입력
        if(serverName.equals("pomeranian_sso")) {
        	log.info(serverName+" shell 실행-------> 1");
        	channelExec.setCommand("cd /neonexsoft/apps/iwest/pomeranian_sso/bin ; ./tomcat.sh start ");	
        }else if (serverName.equals("pomeranian_gasmon_m")) {
        	log.info(serverName+" shell 실행-------> 2");
        	channelExec.setCommand("cd /neonexsoft/apps/iwest/pomeranian_gasmon_m/bin ; ./tomcat.sh start ");        	    
        }else if (serverName.equals("pomeranian_heavymachine")) {
        	log.info(serverName+" shell 실행-------> 3");
        	channelExec.setCommand("cd /neonexsoft/apps/iwest/pomeranian_heavymachine/bin/ ; ./tomcat.sh start ");
        }
          
        
        // 명령어의 실행 결과를 받을 준비를 합니다.
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
                
            	System.out.println(outputBuffer.toString());
                           
                // SSH 연결을 종료하고, 결과값을 반환                                             
            }
            Thread.sleep(10000); // 25초
            channel.disconnect();
            session.disconnect();  
            
            return restartCheck(serverName);
        } 
				
    }
	
	
	
public boolean restartCheck(String serverName) throws Exception{
		
		log.info("프로세스 재기동 체크 ... "+ serverName);
								
							
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
        
        // 실행시킬 명령어를 입력
        
        channelExec.setCommand("ps -ef | grep "+ serverName);	
                      
        
        // 명령어의 실행 결과를 받을 준비를 합니다.
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
                
            	
            	boolean bool = outputBuffer.toString().contains("-DSERVICE_NAME="+serverName);
            	System.out.println("-DSERVICE_NAME="+serverName);
            	
            	System.out.println("outputBuffer.toString()--> " + outputBuffer.toString());
                           
                // SSH 연결을 종료하고, 결과값을 반환
                channel.disconnect();
                session.disconnect();
                
                return bool;
                
            }      
        } 
				
    }
}

	

