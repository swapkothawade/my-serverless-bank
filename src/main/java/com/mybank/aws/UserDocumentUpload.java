package com.mybank.aws;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.UserLogin;


public class UserDocumentUpload implements RequestHandler<HttpRequest, HttpResponse> {

    @Override
    public HttpResponse handleRequest(HttpRequest request, Context context) {
        context.getLogger().log("Input: " + request.toString());
        Gson gson = new Gson();
        UserLogin user = gson.fromJson(request.getBody(), UserLogin.class);
        return new HttpResponse(HttpStatus.SC_OK,processRequest(user));
    }
    
    /**
     * Add your business logic here
     * @param user
     * @return
     */
    private String processRequest(UserLogin user){
    	return String.format("Hello %s , %s from Lambda!",user.getName().toUpperCase(),user.getLastname().toUpperCase());
    }

}
