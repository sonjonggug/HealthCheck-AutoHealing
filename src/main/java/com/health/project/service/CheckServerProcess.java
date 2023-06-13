package com.health.project.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckServerProcess {
		
	
	@Value("${dev.server.id}")
	 String id;
	
	@Value("${dev.server.pw}")
	 String pw;
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
		
	
public ArrayList<List<String>> serverConnect(ArrayList<List<String>> list) {
		
		log.info("프로세스 상태 체크 ... " );
				
		try {
										
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
            	            	            	
            	for(int i = 0; i < list.size(); i++) {              		            		            		
            		if(outputBuffer.toString().contains("-DSERVICE_NAME="+list.get(i).get(0))) {
            			System.out.println("프로세스 정상---> " + list.get(i).get(0));
            			list.get(i).set(3, "Y"); // 프로세스 상태 유무
            			list.get(i).set(4, "0"); // 프로세스 장애 횟수 : 0 정상 / 1~ 장애
            		} else {
            			System.out.println("프로세스 다운---> " + list.get(i).get(0));
            			list.get(i).set(3, "N");            			          		
            		}            		                      	
            	}     
            	  // SSH 연결을 종료하고, 결과값을 반환
            	channel.disconnect();
            	session.disconnect();  
            	return list;                                                                  
            }          		
        }  
			} catch (Exception e) {
				e.printStackTrace();
			}
				return list;	
    }
			
	/**
	 * 프로세스가 다운됬을 시 재 실행
	 * @return
	 * @throws Exception
	 */
	public List<String> restart(List<String> list) throws Exception{
		
		log.info("프로세스 다운 재기동... " + list.get(0));
								
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
	    
	    // 채널 열기
	    Channel channel = session.openChannel("shell");

	    // IO 스트림 설정
	    OutputStream outputStream = channel.getOutputStream();
	    InputStream inputStream = channel.getInputStream();

	    // 셸 실행
	    channel.connect();
	    
	    // 셸 입력
     	outputStream.write((list.get(1) + "\n").getBytes());
     	outputStream.write((list.get(2) + "\n").getBytes()); 
     	
	    // 셸 종료
	    outputStream.write("exit\n".getBytes());
	    outputStream.flush();

	    // 결과 출력
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    String line;
	    
	    while ((line = bufferedReader.readLine()) != null) {
	        log.info(line);
	    }
	    
	    Thread.sleep(10000);
	    
	    // 자원 해제
	    bufferedReader.close();
	    inputStream.close();
	    outputStream.close();
	    channel.disconnect();
	    session.disconnect();      	    	    	
	    	
	    
	        return restartCheck(list);
				
    }
	
	
/**
 * 프로세스 재기동 제대로 됬는지 체크
 * @param list
 * @return
 * @throws Exception
 */
public List<String> restartCheck(List<String> list) throws Exception{
		
		log.info("프로세스 재기동 체크 ... "+ list.get(0));
								
							
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
        
        channelExec.setCommand("ps -ef | grep "+ list.get(0));	
                      
        
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
                
            	
            	boolean bool = outputBuffer.toString().contains("-DSERVICE_NAME="+list.get(0));
            	
            	if(bool) { 
            		log.info(list.get(0) +" 프로세스 재기동 성공");
            		list.set(3, "Y"); // 프로세스 상태 유무 
            		list.set(4, "0"); // 프로세스 장애 횟수 : 0 정상 / 1~ 장애
            	} else {
            		log.info(list.get(0) +" 프로세스 재기동 실패");            		
        			int errSum = Integer.parseInt(list.get(4)) + 1; 
        			list.set(3, "N"); // 프로세스 상태 유무
        			list.set(4, String.valueOf(errSum)); // 프로세스 다운 시 장애 카운트+1
            		}            	            	            	            	
                             	
            	
                // SSH 연결을 종료하고, 결과값을 반환
                channel.disconnect();
                session.disconnect();
                
                return list;
                
            }      
        } 
				
    }
	
    
			
 }





	

