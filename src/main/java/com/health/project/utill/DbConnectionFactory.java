package com.health.project.utill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DbConnectionFactory {
	private static SqlSessionFactory chkDbSqlSessionFactory;
	
	public static SqlSessionFactory getChkDbSqlSessionFactory() {
		
		if (chkDbSqlSessionFactory == null) {
			chkDbSqlSessionFactory = new DbConnectionFactory().createSession("dbCheckConfig.xml");
		}
		
        return chkDbSqlSessionFactory;
    }
	

	private SqlSessionFactory createSession(String path) {
		
		SqlSessionFactory sqlSessionObject = null;
		
		try {
			String resource = path;
			Reader reader = Resources.getResourceAsReader(resource);

			sqlSessionObject = new SqlSessionFactoryBuilder().build(reader);
			
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException iOException) {
			iOException.printStackTrace();
		}
		
		return sqlSessionObject;
		
	}
    
}
