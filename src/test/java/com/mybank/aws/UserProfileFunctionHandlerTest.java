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
public class UserProfileFunctionHandlerTest {

    private static HttpRequest request;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
    	request = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testUserProfileFunctionHandler() {
        UserProfileFunctionHandler handler = new UserProfileFunctionHandler();
        Context ctx = createContext();

        HttpResponse response = handler.handleRequest(request, ctx);

        // TODO: validate output here if needed.
        Assert.assertNotNull(response);
    }
}
