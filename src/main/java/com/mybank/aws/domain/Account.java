package com.mybank.aws.domain;

public class Account {
	
	
	private long accountnumber;
	private AccountType accountType;
	
	public Account() {
		super();
	}
	
	public long getAccountnumber() {
		return accountnumber;
	}
	public void setAccountnumber(long accountnumber) {
		this.accountnumber = accountnumber;
	}
	
	public AccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	
	 

}
