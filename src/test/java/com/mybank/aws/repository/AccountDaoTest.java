package com.mybank.aws.repository;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mybank.aws.domain.AccountTransaction;
import com.mybank.aws.domain.Address;
import com.mybank.aws.domain.AwsUserAccountProfile;

public class AccountDaoTest {
	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private UserAccountDao accountDao = null;

	@Before
	public void setUp() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		accountDao = new UserAccountDao(mongoclient, "mybank");
	}

	@Test
	public void getAccountStatment() {
		String accountid = "1015568897";
		List<AccountTransaction> transactions = accountDao.getAccountStatment(accountid);
		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Gson gson = new Gson();
		String responseBody = gson.toJson(transactions, transactions.getClass());
		System.out.println(responseBody);
	}
	
	
	@Test
	public void saveUserAccountDetailsTest(){
		AwsUserAccountProfile user = getUSerDetails();
		String accountType = "checking";
		try {
			accountDao.saveUserAccountDetails(user,accountType);
		} catch(MongoWriteException exception){
			exception.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateUserAccountDetailsTest(){
		AwsUserAccountProfile user = getUserDetailsForUpdate();
		accountDao.updateUserAccountDetails(user);
	}
	
	
	@Test
	public void getUserProfileByUserName(){
		String username = "user10";
		accountDao.getUserAccountDetailsbyUserName(username);
	}
	
	@Test
	public void getUserProfileByAccountNumber(){
		long accountNumber=1029193387;
		AwsUserAccountProfile  userAccountProfile = accountDao.getUserAccountDetailsbyAccountNumber(accountNumber);
		assertNotNull(userAccountProfile);
		assertEquals(1,userAccountProfile.getAccounts().stream().filter(account->account.getAccountnumber() == accountNumber).count());
	}
	
	@Test
	public void getUserProfileByAccountNumberNA(){
		long accountNumber=1029193385;
		AwsUserAccountProfile  userAccountProfile = accountDao.getUserAccountDetailsbyAccountNumber(accountNumber);
		assertNull(userAccountProfile);
		//assertEquals(0,userAccountProfile.getAccounts().stream().filter(account->account.getAccountnumber() == accountNumber).count());
	}

	private AwsUserAccountProfile getUSerDetails() {
		AwsUserAccountProfile user = new AwsUserAccountProfile();
		user.setUserName("user10");
		
		user.setFirstName("Sachin");
		user.setLastName("Tendulkar");
		
		user.setContactNo("+12568993690");
		user.setUserAddress(getAddress());
		user.setPan("1234354565");
		user.setDob(LocalDate.of(1973, 4, 24));
		user.setEmail("sachin.tendulkar@gmail.com");
		return user;
	}

     private Address getAddress() {
        Address communicationAddress = new Address();
        communicationAddress.setStreet("169 Manhattan Avenue");
        communicationAddress.setCity("Jersey City");
        communicationAddress.setState("New Jerssey");
        communicationAddress.setZip(07307);
        return communicationAddress;
    }
     
     private AwsUserAccountProfile getUserDetailsForUpdate() {
 		AwsUserAccountProfile user = new AwsUserAccountProfile();
 		user.setUserName("user10");
 		user.setFirstName("Sachin");
 		user.setLastName("Tendulkar");
 		user.setContactNo("+919986959669");
 		user.setUserAddress(getAddressForUpdate());
 		user.setPan("1234354565");
 		user.setDob(LocalDate.of(1973, 6, 28));
 		user.setEmail("sachin.tendulkar@gmail.com");
 		return user;
 	}
     
     private Address getAddressForUpdate() {
         Address communicationAddress = new Address();
         communicationAddress.setStreet("2 Sachin");
         communicationAddress.setCity("Juhu, Mumbai");
         communicationAddress.setState("India");
         communicationAddress.setZip(400001);
         return communicationAddress;
     }

}
