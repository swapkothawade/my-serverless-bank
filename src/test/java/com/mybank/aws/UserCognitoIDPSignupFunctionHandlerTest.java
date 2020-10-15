package com.mybank.aws;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserCognitoIDPSignupFunctionHandlerTest {

    private static HttpRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
       // TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        //ctx.setFunctionName("Your Function Name");

        return null;
    }

    @Test
    public void testUserCognitoIDPSignupFunctionHandler() {
        UserCognitoIDPSignupFunctionHandler handler = new UserCognitoIDPSignupFunctionHandler();
        Context ctx = createContext();

        HttpResponse output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
