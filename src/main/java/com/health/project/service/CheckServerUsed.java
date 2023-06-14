package com.health.project.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckServerUsed {
	
	
	@Value("${dev.server.id}")
	 String id;
	
	@Value("${dev.server.pw}")
	 String pw;
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
		
	

			
	/**
	 * 프로세스가 다운됬을 시 재 실행
	 * @return
	 * @throws Exception
	 */
	public boolean checkRamUsed(){
		
	try {
		
	
		log.info("사용률 체크... " + "");
								
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
	    
	    // 채널 연결
	    channel.connect();
	    
	    // 셸 입력
	    
	    outputStream.write(("free -h | awk 'NR==2 {print $7}'" + "\n").getBytes()); // 사용가능한 avliable 용량 체크 ( available은 시스템에서 현재 사용 가능한 메모리의 양 )
	    outputStream.write(("top -bn 1 | awk '/%Cpu/ { print $2 \"%!\" }'" + "\n").getBytes()); // CPU 사용률 , 값 추출을 위해 사용량 %! 으로 출력후 변환
	    outputStream.write(("df -h | awk 'NR>1 && int(substr($5, 1, length($5)-1)) > 80 {gsub(/%/, \"%_\"); print}'" + "\n").getBytes()); // 디스크 사용량 80퍼 초과 시 , 값 추출을 위해 사용량 %_ 으로 출력후 변환
     	     	
	    // 셸 종료
	    outputStream.write("exit\n".getBytes());
	    outputStream.flush();

	    // 결과 출력
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    
	    //값 담을 변수들
	    String line;	    
	    String ramUsed = "";
	    String cpuUsed = "";
	    ArrayList<String> diskUsed = new ArrayList<String>();
	    
	    // line별 읽어들이면서 특정값 포함된 값 저장 
	    while ((line = bufferedReader.readLine()) != null) {	    	
	    	if(line.contains("Gi")) {
	    		ramUsed=line.replace("Gi", "GB");	    		
	    	}else if (line.contains("%!")){
	    		cpuUsed=line.replace("%!", "%");
	    	}else if (line.contains("%_")) {
	    		if(!line.contains("{")) {
	    			diskUsed.add(line.replace("%_", "%"));
	    		}	    		
	    	};	    		    	       
	    }
	    
	    InetAddress localhost = InetAddress.getLocalHost();
	                    
	    System.out.println("ramUsed : " + ramUsed);
	    System.out.println("cpuUsed : " + cpuUsed);
	    
	    for(int i =0; i < diskUsed.size(); i++) {
	    	System.out.println("diskUsed : "+diskUsed.get(i));
	    }
	    
	    System.out.println("HostName : " + localhost.getHostName());
//	    System.out.println(localhost.getHostAddress());
	    
	    // 자원 해제
	    bufferedReader.close();
	    inputStream.close();
	    outputStream.close();
	    channel.disconnect();
	    session.disconnect();      	    	    	
	    		    	        
		} catch (Exception e) {
			e.printStackTrace();
			return false;		
		}	
		return true;
    }
	
	
}