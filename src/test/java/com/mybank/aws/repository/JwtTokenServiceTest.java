package com.mybank.aws.repository;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class JwtTokenServiceTest {
	private JwtTokenService tokenService = null;
	private String username="swapnil.kothawade@gmail.com";
	@Before
	public void setUp(){
		tokenService = new JwtTokenService();
	}
	
	@Test
	public void createTokenTest(){
		String token = tokenService.createToken(username, Arrays.asList("USER"));
		System.out.println("Token " + token);
		assertNotNull(token);
	}

	

	@Test
	public void createTokenInvalidUserTest(){
		String token = tokenService.createToken("swapnil!23wr", Arrays.asList("USER"));
		assertNotNull(token);
	}

}
