package com.mybank.aws;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserSessionDao;

public class UserSignoutFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {
	private UserSessionDao userSessionDao = null;
	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private final String MY_DATABSE = "mybank";

	public UserSignoutFunctionHandler() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		userSessionDao = new UserSessionDao(mongoclient, MY_DATABSE);

	}
	
    @Override
    public HttpResponse handleRequest(HttpRequest request, Context context) {
        context.getLogger().log("Input: " + request);
        String token = request.getHeaders().get("Authorization");
        boolean flag = userSessionDao.deleteUserSession(token);
        String responseBody = flag? "User Session Deleted Successfully!!!" : "User Session Not Deleted, Please try again!!!";
        
        return new HttpResponse(HttpStatus.SC_OK,responseBody);
    }

}
