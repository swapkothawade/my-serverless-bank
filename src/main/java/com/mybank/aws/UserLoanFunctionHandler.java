package com.mybank.aws;

import java.time.LocalDate;

import org.apache.http.HttpStatus;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.Loan;
import com.mybank.aws.domain.SNSLoanMessageAttributes;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserLoanDao;

public class UserLoanFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

	private UserLoanDao userLoanDao = null;
	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private final String MY_DATABSE = "mybank";
	Gson gson = new Gson();

	public UserLoanFunctionHandler() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		userLoanDao = new UserLoanDao(mongoclient, MY_DATABSE);

	}

	/**
	 * Implementation pending for View loan
	 */

	@Override
	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Input: " + request);
		context.getLogger().log("***Request Path : " + request.getPath());
		context.getLogger().log("***Request Resources  : " + request.getResource());
		String methodName = request.getHttpMethod();
		if ("GET".equalsIgnoreCase(methodName)) {
			String loanid = request.getPathParameters().get("loanid");
			Loan loan = userLoanDao.getLoanDetailsbyLoanId(loanid);
			String responseBody = gson.toJson(loan, Loan.class);
			return new HttpResponse(HttpStatus.SC_OK, responseBody);

		} else if ("POST".equalsIgnoreCase(methodName)) {
			Loan userLoan = gson.fromJson(request.getBody(), Loan.class);
			userLoan.setLoanApplicationDate(LocalDate.now());
			userLoan.setStatus("PENDING");
			String loanid = userLoanDao.save(userLoan);
			if (loanid != null) {
				userLoan.setLoanid(loanid);
				String message = gson.toJson(userLoan);
				publishMessage(message);
				return new HttpResponse(HttpStatus.SC_OK,
						String.format("Loan Applied Successfully, your loan id is %s", loanid));
			} else {
				return new HttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,
						"Something went wrong, please try again in sometime");
			}
		} else if ("PUT".equalsIgnoreCase(methodName)) {
			return null;
		} else if ("DELETE".equalsIgnoreCase(methodName)) {
			return null;
		} else {
			return new HttpResponse(HttpStatus.SC_BAD_REQUEST, "Unsupported Operation!!!");
		}

	}

	private AmazonSNS getSNSClient() {
		ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		
		AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withCredentials(propertiesFileCredentialsProvider)
				.withRegion(Regions.US_EAST_2).build();
		return snsClient;
	}

	private void publishMessage(String messageBody) {

		SNSLoanMessageAttributes sns = new SNSLoanMessageAttributes(messageBody);
		sns.publish(getSNSClient(), "arn:aws:sns:us-east-2:381233585660:loan-request-sns");
	}

}
