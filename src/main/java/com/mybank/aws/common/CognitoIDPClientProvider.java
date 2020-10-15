package com.mybank.aws.common;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;

public class CognitoIDPClientProvider {

	private final String CLIENT_ID = "*******";
	private final String USER_POOL_ID = "*****";
	private String endpoint = "******";
	private static String region = "******";

	public static AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
		ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

		return AWSCognitoIdentityProviderClientBuilder.standard().withCredentials(propertiesFileCredentialsProvider)
				.withRegion(region).build();

	}

	public static GetUserResult getUserDetails(String accessToken) {
		GetUserResult user = getAmazonCognitoIdentityClient()
				.getUser(new GetUserRequest().withAccessToken(accessToken));
		return user;
	}

}
