package com.mybank.aws;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;

public class HelloLambdaFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

    @Override
    public HttpResponse handleRequest(HttpRequest request, Context context) {
        context.getLogger().log("Input: " + request);
        String name = request.getPathParameters().get("name");;
        
        return new HttpResponse(HttpStatus.SC_OK,String.format("Hello %s,Welcome to my websitee", name));
    }

}



//aws cognito-idp admin-initiate-auth --user-pool-id us-east-2_Sv1KdHOh8 --client-id 7tgsf9m7u2jcervqess71r8lcd --auth-flow ADMIN_NO_SRP_AUTH --auth-parameters USERNAME=user1,PASSWORD=P@ssword123 --region us-east-2

// After resetting password, use below command to request new tokens
//aws cognito-idp admin-initiate-auth --user-pool-id us-east-2_Sv1KdHOh8 --client-id 7tgsf9m7u2jcervqess71r8lcd --auth-flow ADMIN_NO_SRP_AUTH --auth-parameters USERNAME=user1,PASSWORD=User@2020 --region us-east-2

//aws cognito-idp admin-respond-to-auth-challlenge --user-pool-id us-east-2_Sv1KdHOh8 --client-id 7tgsf9m7u2jcervqess71r8lcd --challenge-name NEW_PASSWORD_REQUIRED --CHALLLENGE-RESPONSES USERNAME=user1,NEW_PASSWORD=P$ssword123 --region us-east-2


//aws cognito-idp admin-initiate-auth --user-pool-id us-east-2_Sv1KdHOh8 --client-id 7tgsf9m7u2jcervqess71r8lcd --auth-flow ADMIN_NO_SRP_AUTH --auth-parameters USERNAME=user1,PASSWORD=User@2020 --region us-east-2