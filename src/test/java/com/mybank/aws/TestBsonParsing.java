package com.mybank.aws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.gson.Gson;
import com.mybank.aws.domain.UserProfile;

public class TestBsonParsing {

	@Test
	public void testBsonparsing(){
		String jsonFile ="/Users/superdev/Downloads/myprojects/aws-repo/eclipse/MyBankAws/example.json" ;
		UserProfile[] users = null;
		try {
			InputStreamReader isr = new InputStreamReader( new FileInputStream(new File(jsonFile)));
			BufferedReader reader = new BufferedReader(isr);
			
			Gson gson = new Gson();
			users = gson.fromJson(reader, UserProfile[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		System.out.println(users[0].toString());
		
		
	}
	
}
