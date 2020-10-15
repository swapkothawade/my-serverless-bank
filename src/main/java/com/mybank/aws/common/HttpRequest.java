package com.mybank.aws.common;

import java.util.Map;

public class HttpRequest {

	private Map<String, String> headers;
	private Map<String, String> queryStringParameters;
	private Map<String, String> pathParameters;
	private String body;
	private String httpMethod;
	private String path;
	private String resource;

	public HttpRequest() {

	}

	public HttpRequest(Map<String, String> headers, Map<String, String> queryStringParameters,
			Map<String, String> pathParameters, String body, String httpMethod,String resource) {
		super();
		this.headers = headers;
		this.queryStringParameters = queryStringParameters;
		this.pathParameters = pathParameters;
		this.body = body;
		this.httpMethod = httpMethod; 
		this.resource = resource;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getQueryStringParameters() {
		return queryStringParameters;
	}

	public void setQueryStringParameters(Map<String, String> queryStringParameters) {
		this.queryStringParameters = queryStringParameters;
	}

	public Map<String, String> getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "HttpRequest [headers=" + headers + ", queryStringParameters=" + queryStringParameters
				+ ", pathParameters=" + pathParameters + ", body=" + body + "]";
	}

}
