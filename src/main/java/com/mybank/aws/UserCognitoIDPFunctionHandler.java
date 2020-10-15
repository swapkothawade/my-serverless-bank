package com.mybank.aws;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mybank.aws.common.CognitoIDPClientProvider;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.dto.LoginRequest;

public class UserCognitoIDPFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

	private final String CLIENT_ID = "***";
	private final String USER_POOL_ID = "****";
	private String endpoint = "****";
	private String region = "**";
	Gson gson = new Gson();


	@Override
	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Input: " + request);

		LoginRequest loginRequest = gson.fromJson(request.getBody(), LoginRequest.class);

		// AWSCognitoIdentityProvider client = getAmazonCognitoIdentityClient();
		AWSCognitoIdentityProvider client = CognitoIDPClientProvider.getAmazonCognitoIdentityClient();
		context.getLogger().log("Input: " + request);

		final Map<String, String> authParams = new HashMap<>();
		authParams.put("USERNAME", loginRequest.getUsername());
		authParams.put("PASSWORD", loginRequest.getPassword());

		final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
		authRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH).withClientId(CLIENT_ID).withUserPoolId(USER_POOL_ID)
				.withAuthParameters(authParams);

		AdminInitiateAuthResult result = client.adminInitiateAuth(authRequest);
		AuthenticationResultType authResult = result.getAuthenticationResult();
		String responseBody = gson.toJson(authResult, AuthenticationResultType.class);

		return new HttpResponse(HttpStatus.SC_OK, responseBody);
	}

	/*
	 * //Extracted in utility class as sttatic method public
	 * AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
	 * ClasspathPropertiesFileCredentialsProvider
	 * propertiesFileCredentialsProvider = new
	 * ClasspathPropertiesFileCredentialsProvider();
	 * 
	 * return
	 * AWSCognitoIdentityProviderClientBuilder.standard().withCredentials(
	 * propertiesFileCredentialsProvider) .withRegion(region).build();
	 * 
	 * }
	 */
}
