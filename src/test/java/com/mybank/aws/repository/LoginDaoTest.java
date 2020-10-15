package com.mybank.aws.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.client.MongoClient;
import com.mybank.aws.domain.UserLogin;;
public class LoginDaoTest {
	
	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private LoginDao loginDao = null;
	@Before
	public void setUp(){
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		loginDao = new LoginDao(mongoclient,"mybank");
	}
	
	
	@Test
	public void getUserTest(){
		UserLogin user = loginDao.getUser("swapnil.kothawade@gmail.com");
		assertNotNull(user);
		assertEquals("Swapnil",user.getName());
		
	}
	

}
