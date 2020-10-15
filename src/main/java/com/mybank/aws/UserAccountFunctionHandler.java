package com.mybank.aws;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.AccountType;
import com.mybank.aws.domain.AwsUserAccountProfile;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserAccountDao;

/**
 * Responsible for dealing with account profile
 * 
 * @author superdev
 *
 */
public class UserAccountFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

	private UserAccountDao userAccountDao = null;
	private String CONN_STRING = "mongodb://root:root%402020@<hosturl>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private final String MY_DATABSE = "mybank";
	Gson gson = new Gson();

	public UserAccountFunctionHandler() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		userAccountDao = new UserAccountDao(mongoclient, MY_DATABSE);

	}

	@Override
	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Input: " + request);
		String methodName = request.getHttpMethod();
		String resourcePath = request.getPath();
		String resource = request.getResource();
		
		context.getLogger().log(String.format("Resource Path  %s, Resource   %s \n", resourcePath, resource));
		
		String responseBody = String.format("Request Method Received for %s", methodName);

		if ("GET".equalsIgnoreCase(methodName)) {
			// Need to extract username and email from token
			String username = request.getPathParameters().get("username");
			context.getLogger().log(String.format("Request Method %s, Username ID %s \n", methodName, username));
			AwsUserAccountProfile awsProfile = userAccountDao.getUserAccountDetailsbyUserName(username);
			responseBody = gson.toJson(awsProfile, AwsUserAccountProfile.class);
			context.getLogger().log("Response Received  "+responseBody);
		} else if ("PUT".equalsIgnoreCase(methodName)) {
			AwsUserAccountProfile awsProfile = gson.fromJson(request.getBody(), AwsUserAccountProfile.class);
			userAccountDao.updateUserAccountDetails(awsProfile);
			responseBody = String.format("Profile information for %s updated successfullly", awsProfile.getFirstName());
		} else if ("DELETE".equalsIgnoreCase(methodName)) {

		} else if ("POST".equalsIgnoreCase(methodName)) {
			String accountType = request.getQueryStringParameters().get("type");
			AccountType actType = AccountType.SAVING;
			actType = accountType.equalsIgnoreCase("saving") ? AccountType.SAVING : AccountType.CHECKING;
			AwsUserAccountProfile awsProfile = gson.fromJson(request.getBody(), AwsUserAccountProfile.class);
			try {
				userAccountDao.saveUserAccountDetails(awsProfile, actType.getValue());
				responseBody = String.format("User Account created for %s", awsProfile.getFirstName());
			} catch (Exception e) {
				return new HttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());

			}
		} else {
			return new HttpResponse(HttpStatus.SC_BAD_REQUEST, "Unsupported Operation!!!");
		}

		HttpResponse response = new HttpResponse(HttpStatus.SC_OK, responseBody);
		return response;

	}
	// Ideally catch exception a,log it and then throw custom exception

}

//"{\"userName\":\"user11\",\"firstName\":\"Swara\",\"lastName\":\"Kothawade\",\"email\":\"swara.kothawade@gmail.com\",\"userAddress\":{\"street\":\"169 Manhattan\",\"city\":\"Jersey City\",\"state\":\"USA\",\"zip\":07307},\"pan\":\"3699631234\",\"contactNo\":\"+12017368236\",\"dob\":{\"year\":2019,\"month\":12,\"day\":19}}"
