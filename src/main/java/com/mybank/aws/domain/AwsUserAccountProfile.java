package com.mybank.aws.domain;

import java.time.LocalDate;
import java.util.Set;

/**
 * username and email can be taken from token. Need to explore Cognito for
 * getting this information
 * 
 * @author superdev
 *
 */
public class AwsUserAccountProfile {

	public AwsUserAccountProfile() {
		super();
	}

	private String userName;
	
	private String firstName;

	private String lastName;

	private String email;

	private Address userAddress;

	private String pan;

	private String contactNo;

	private LocalDate dob;
	
	private Set<Account> accounts;

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(Address userAddress) {
		this.userAddress = userAddress;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	

}
