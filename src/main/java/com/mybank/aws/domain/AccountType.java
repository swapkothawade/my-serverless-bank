package com.mybank.aws.domain;

public enum AccountType {
	SAVING("saving"),
    CHECKING("cheking");
    String value;
    AccountType(String accountType) {
        this.value = accountType;
    }

    public String getValue() {
        return value;
    }
}
