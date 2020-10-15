package com.mybank.aws;

import java.util.Arrays;

import org.apache.http.HttpStatus;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.UserLogin;
import com.mybank.aws.dto.LoginRequest;
import com.mybank.aws.repository.JwtTokenService;
import com.mybank.aws.repository.LoginDao;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserSessionDao;

public class UserLoginFunction implements RequestHandler<HttpRequest, HttpResponse> {

	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private LoginDao loginDao = null;
	private UserSessionDao sessionDao = null;
	private final String MY_DATABSE = "mybank";

	public UserLoginFunction() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		loginDao = new LoginDao(mongoclient, MY_DATABSE);
		sessionDao = new UserSessionDao(mongoclient,MY_DATABSE);
	}

	// this code is for reference purpose only
	/*
	 * @Override public HttpResponse handleRequest(HttpRequest request, Context
	 * context) { context.getLogger().log("Input: " + request);
	 * 
	 * Region region = Region.US_EAST_1; S3Client s3client =
	 * S3Client.builder().region(region).build(); ResponseInputStream<?>
	 * response = s3client
	 * .getObject(GetObjectRequest.builder().bucket("bfs-customer-document").key
	 * ("example.json").build());
	 * 
	 * InputStreamReader isr = new InputStreamReader(response); BufferedReader
	 * reader = new BufferedReader(isr); Gson gson = new Gson(); User[] users =
	 * gson.fromJson(reader, User[].class);
	 * 
	 * return users[0].toString(); }
	 */

	@Override
	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Request Recieved for : " + request.toString());
		Gson gson = new Gson();
		LoginRequest loginCredentials = gson.fromJson(request.getBody(), LoginRequest.class);

		return processRequest(loginCredentials);
	}

	/**
	 * Add your business logic here
	 * 
	 * @param user
	 * @return private HttpResponse processRequest(LoginRequest
	 *         loginCredentials) { User user =
	 *         loginDao.getUser(loginCredentials.getUsername()); if (user !=
	 *         null &&
	 *         user.getPassword().equals(loginCredentials.getPassword())) {
	 *         return getToken(user); } else { return new
	 *         HttpResponse(HttpStatus.SC_UNAUTHORIZED, String.format(
	 *         "Hello %s , %s, You are not authorized user!",
	 *         loginCredentials.getUsername().toUpperCase(),
	 *         loginCredentials.getPassword().toUpperCase())); }
	 * 
	 *         }
	 */

	/**
	 * call another lambda function for token related service, having some issue, will deal with later
	 * @param user
	 * @return
	 */
	private HttpResponse getToken(UserLogin user) {
		String functionName = "JwtTokenServiceFunction";
		AWSLambda client = AWSLambdaClient.builder().withRegion(Regions.US_EAST_2).build();
		Gson gson = new Gson();
		String payload = gson.toJson(user);
		InvokeRequest request = new InvokeRequest();
		request.withFunctionName("JwtTokenServiceFunction").withPayload(payload);
		InvokeResult invoke = client.invoke(request);
		System.out.println("Result invoking " + functionName + ": " + invoke);
		return new HttpResponse(invoke.getStatusCode().intValue(), new String(invoke.getPayload().array()));
	}

	private HttpResponse processRequest(LoginRequest loginCredentials) {
		UserLogin user = loginDao.getUser(loginCredentials.getUsername());

		String token = "this is my token";

		if (user != null && user.getPassword().equals(loginCredentials.getPassword())) {
			JwtTokenService tokenProvider = new JwtTokenService();
			token = tokenProvider.createToken(user.getEmail(), Arrays.asList(new String[] { "USER" }));
			sessionDao.saveUserSession(loginCredentials.getUsername(),token);
			// TODO: add into session table
			return new HttpResponse(HttpStatus.SC_OK, token);
		} else {
			return new HttpResponse(HttpStatus.SC_UNAUTHORIZED,
					String.format("Hello %s , %s, You are not authorized user!",
							loginCredentials.getUsername().toUpperCase(),
							loginCredentials.getPassword().toUpperCase()));
		}

	}

}
