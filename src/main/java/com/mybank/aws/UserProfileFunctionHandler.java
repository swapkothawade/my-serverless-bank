package com.mybank.aws;

import java.util.List;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.UserProfile;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserProfileDao;

/**
 * This function should handle below functionality get
 * /restricted/profile/{email} post
 * /public/signin @PutMapping("/restricted/profile")
 */
public class UserProfileFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private UserProfileDao userProfileDao = null;
	private Gson gson = new Gson();
	private final String MY_DATABSE = "mybank";

	public UserProfileFunctionHandler() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		userProfileDao = new UserProfileDao(mongoclient, MY_DATABSE);

	}

	@Override
	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Request Payload " + request.getBody());
		String methodName = request.getHttpMethod();
		context.getLogger().log("Request Method Received for " + methodName);
		String responseBody = String.format("Request Method Received for %s", methodName);
		if ("GET".equalsIgnoreCase(methodName)) {
			String userName = request.getPathParameters().get("email");
			context.getLogger().log("Request Received for " + userName);
			UserProfile userProfile = userProfileDao.getUserProfileByEmail(userName);
			responseBody = gson.toJson(userProfile, UserProfile.class);
		} else if ("PUT".equalsIgnoreCase(methodName)) {
			UserProfile userProfile = gson.fromJson(request.getBody(), UserProfile.class);
			userProfileDao.updateProfile(userProfile);
			responseBody = String.format("User %s,%s Updated Successfully!!!", userProfile.getLastName(),
					userProfile.getFirstName());
		} else if ("DELETE".equalsIgnoreCase(methodName)) {
			// We should disabled user in login_detail table. 
			String userName = request.getPathParameters().get("email");
			context.getLogger().log("Delete Request Received for " + userName);

			boolean userProfile = userProfileDao.deleteProfile(userName);
			if (userProfile) {
				responseBody = String.format("User %s Deleted Successfully!!!", userName);
			} else {
				responseBody = String.format("User %s Not Deleted, Pelase cheeck username is correct!!!", userName);
			}
			
		} else if ("POST".equalsIgnoreCase(methodName)) {
			UserProfile userProfile = gson.fromJson(request.getBody(), UserProfile.class);
			userProfileDao.addUser(userProfile);
			responseBody = String.format("User %s,%s Added Successfully!!!", userProfile.getLastName(),
					userProfile.getFirstName());
		} else {
			List<UserProfile> profiles = userProfileDao.findAll();
			responseBody = gson.toJson(profiles,UserProfile.class);
		}

		HttpResponse response = new HttpResponse(HttpStatus.SC_OK, responseBody);
		return response;
	}

}
