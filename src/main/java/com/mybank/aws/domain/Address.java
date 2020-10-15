package com.mybank.aws.domain;

public class Address {
	private String street;
	private String city;
	private String state;
	private String country;
	private int zip;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street; 
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}
}
