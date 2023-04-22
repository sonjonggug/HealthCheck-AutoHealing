package com.health.project.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Service
public class HealthCheckService {

	@Value("${cloud.server.id}")
	 String id;
	
	@Value("${cloud.server.pw}")
	 String pw;
	
	@Value("${cloud.server.ip}")
	 String ip;
	
	@Value("${cloud.server.port}")
	 int port;
	
	
	public boolean serverConnect() throws Exception{
		
		System.out.println("id" + id);
		System.out.println("id" + pw);
		System.out.println("id" + ip);
		System.out.println("id" + port);
		
        JSch jsch = new JSch();
        Session session = jsch.getSession(id, ip , port);
        session.setPassword(pw);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();  //연결 시도
            
        Channel channel = session.openChannel("exec");  // 채널접속  exec = 명령을 요청하기 위함 
        ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand("ps -ef | grep CheckLinJi"); // 내가 실행시킬 명령어를 입력
    
        //콜백을 받을 준비.
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err);             
        channel.connect();  //실행
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, i));
                if (i < 0) break;
            }
            if (channel.isClosed()) {
//                System.out.println(outputBuffer.toString());
//                System.out.println(outputBuffer.toString().contains("DserverName"));
            	System.out.println(outputBuffer.toString());
            	
                boolean bool = outputBuffer.toString().contains("-Dspring.profiles.active=prod");
                
                System.out.println(bool);
                
                channel.disconnect();
                return bool;
                
            }      
        }       
    }
}
