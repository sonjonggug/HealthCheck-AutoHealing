package com.health.project.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class DbCheckDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public DbCheckDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public int dbInsert() {
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.insert("dbCheck.dbCheckInsert");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			session.commit();
			System.out.println(result);
			session.close();
		}

		return result;
	}
	
	public int dbUpdate() {
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.update("dbCheck.dbCheckUpdate");
		} finally {
			session.commit();
			session.close();
		}

		return result;
	}
	
	public int dbDelete() {
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.delete("dbCheck.dbCheckDelete");
		} finally {
			session.commit();
			session.close();
		}

		return result;
	}
}
