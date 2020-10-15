package com.mybank.aws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserDocumentUploadTest {

    private static HttpRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = new HttpRequest();
        
        input.setBody("{\"username\" : \"Swapnil.kothawade@gmail.com\",\"password\":\"Password123\"}");
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
       input.setHeaders(headers);
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testUserDocumentUpload() {
        UserDocumentUpload handler = new UserDocumentUpload();
        Context ctx = createContext();

        HttpResponse output = handler.handleRequest(input, ctx);
        ctx.getLogger().log(output.getBody());
        System.out.println("Body part of Function"+output);
        // TODO: validate output here if needed.
      //  Assert.assertEquals("Hello from Lambda!", output);
    }
}
