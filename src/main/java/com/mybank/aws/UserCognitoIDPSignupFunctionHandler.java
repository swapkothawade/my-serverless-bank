package com.mybank.aws;

import org.apache.http.HttpStatus;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.UserSignUpRequest;

public class UserCognitoIDPSignupFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

	private final String CLIENT_ID = "******";
	private final String USER_POOL_ID = "*********";
	private String endpoint = "*******";
	private String region = "********";
	Gson gson = new Gson();

	@Override
	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Input: " + request);

		UserSignUpRequest userSignuprequest = gson.fromJson(request.getBody(), UserSignUpRequest.class);
		UserType userType = signUp(userSignuprequest);
		String responseBody = String.format("User %s status %s, Please check your email for more details",
				userType.getUsername(), userType.getUserStatus());
		// TODO: implement your handler
		return new HttpResponse(HttpStatus.SC_OK, responseBody);
	}

	public UserType signUp(UserSignUpRequest signUpRequest) {

		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();
		
		AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest().withUserPoolId(USER_POOL_ID)
				.withUsername(signUpRequest.getUsername())
				.withUserAttributes(new AttributeType().withName("email").withValue(signUpRequest.getEmail()),
						new AttributeType().withName("name").withValue(signUpRequest.getName()),
						new AttributeType().withName("family_name").withValue(signUpRequest.getLastname()),
						new AttributeType().withName("phone_number").withValue(signUpRequest.getPhoneNumber()),
						new AttributeType().withName("email_verified").withValue("true"))
				.withTemporaryPassword("Cts@2020").withMessageAction("SUPPRESS")
				.withDesiredDeliveryMediums(DeliveryMediumType.EMAIL).withForceAliasCreation(Boolean.FALSE);
		

		AdminCreateUserResult createUserResult = cognitoClient.adminCreateUser(cognitoRequest);
		UserType cognitoUser = createUserResult.getUser();

		return cognitoUser;

	}

	// In Spring based application, its candidate for bean which can be
	// autowired.
	public AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
		ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

		return AWSCognitoIdentityProviderClientBuilder.standard().withCredentials(propertiesFileCredentialsProvider)
				.withRegion(region).build();

	}
}
