package com.mybank.aws;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.mybank.aws.common.HttpRequest;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserCognitoIDPFunctionHandlerTest {

    private static HttpRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
      return null;
    }

    @Test
    public void testUserCognitoIDPFunctionHandler() {
        UserCognitoIDPFunctionHandler handler = new UserCognitoIDPFunctionHandler();
        Context ctx = createContext();

        com.mybank.aws.common.HttpResponse output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
