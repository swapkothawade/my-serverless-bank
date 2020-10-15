package com.mybank.aws;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.Loan;
import com.mybank.aws.dto.SQSMessageDTO;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserLoanDao;


public class UserLoanRequestCreditCheckFunction implements RequestHandler<SQSEvent, HttpResponse>{
  
    LambdaLogger logger = null;

	private UserLoanDao userLoanDao = null;
	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private final String MY_DATABSE = "mybank";
	Gson gson = new Gson();

	public UserLoanRequestCreditCheckFunction() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		userLoanDao = new UserLoanDao(mongoclient, MY_DATABSE);

	}
    
	@Override
    public HttpResponse handleRequest(SQSEvent event, Context context)
    {
		logger = context.getLogger();
		logger.log("UserLoanRequestCreditCheckFunction Triggered");
		String messageBody;
		
        for(SQSMessage msg : event.getRecords()){
        	messageBody = new String(msg.getBody());
            logger.log("******Message body--> " + messageBody);
           SQSMessageDTO dto = gson.fromJson(messageBody,SQSMessageDTO.class);
           
           logger.log("******SQSMessageDTO****** " + dto.toString()); 
            Loan userLoan = gson.fromJson(dto.getMessage(),Loan.class);
            logger.log("******** Credit check for loan id " + userLoan.getLoanid());
            userLoanDao.updateLoanStatus(userLoan.getLoanid(), "APPROVED");
            logger.log(String.format("Request for Loan id %s got approved, please check your email %s for details",userLoan.getLoanid(),userLoan.getUsername()));
        }
        logger.log("Lambda Resting now");
        return new HttpResponse(HttpStatus.SC_OK,"Credi Check completed for all request");
    }

}


