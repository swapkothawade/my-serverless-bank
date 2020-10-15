package com.mybank.aws.domain;

import java.time.LocalDate;

public class Loan {
	private String loanid;
	private String username;
	private String loanType;
	private double loanAmount;
	private LocalDate loanApplicationDate;
	private float rateOfInterest;
	private int loanDurationInMonth;
	private String status;

	public String getLoanid() {
		return loanid;
	}

	public void setLoanid(String loanid) {
		this.loanid = loanid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public LocalDate getLoanApplicationDate() {
		return loanApplicationDate;
	}

	public void setLoanApplicationDate(LocalDate loanApplicationDate) {
		this.loanApplicationDate = loanApplicationDate;
	}

	public float getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(float rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public int getLoanDurationInMonth() {
		return loanDurationInMonth;
	}

	public void setLoanDurationInMonth(int loanDurationInMonth) {
		this.loanDurationInMonth = loanDurationInMonth;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
