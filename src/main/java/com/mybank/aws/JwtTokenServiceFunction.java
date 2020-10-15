package com.mybank.aws;

import java.util.Arrays;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.UserLogin;
import com.mybank.aws.repository.JwtTokenService;

public class JwtTokenServiceFunction implements RequestHandler<HttpRequest, HttpResponse> {

    @Override
    public HttpResponse handleRequest(HttpRequest request, Context context) {
        context.getLogger().log("Input: " + request);
        String token = "this is my token";
        Gson gson = new Gson();
        UserLogin user =  gson.fromJson(request.getBody(), UserLogin.class);
        JwtTokenService tokenProvider = new JwtTokenService();
       token = tokenProvider.createToken(user.getEmail(), Arrays.asList(new String[] {"USER"}));
        // TODO: implement your handler
        return new HttpResponse(HttpStatus.SC_OK,token);
    }

}
///arn:aws:lambda:us-east-2:381233585660:function:JwtTokenServiceFunction