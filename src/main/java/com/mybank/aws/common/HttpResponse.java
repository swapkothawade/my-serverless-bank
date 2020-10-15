package com.mybank.aws.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

public class HttpResponse {

	private int statusCode = HttpStatus.SC_OK;
	private Map<String, String> headers = new HashMap<>();
	private String body;

	public HttpResponse() {
		headers.put("Content-Type", "application/json");
	}

	public HttpResponse(int statusCode, String responseBody) {
		this();
		this.statusCode = statusCode;
		this.body = responseBody;
	}
 
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers; 
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
