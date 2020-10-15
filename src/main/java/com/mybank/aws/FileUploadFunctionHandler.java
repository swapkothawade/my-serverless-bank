package com.mybank.aws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.http.HttpStatus;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;

public class FileUploadFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {
	
	private List<String> ALLOWED_MIMPE_TYPE = Arrays.asList(
			"application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/jpeg",
			"image/png", "application/pdf");

	public HttpResponse handleRequest(HttpRequest event, Context context) {

		// Create the logger
		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of Proxy");

		// Log the length of the incoming body
		logger.log(String.valueOf(event.getBody().getBytes().length));
		event.getHeaders().entrySet().forEach(item -> System.out.println(item.getKey() + "-->" + item.getValue()));
		// Create the APIGatewayProxyResponseEvent response
		HttpResponse response = null;

		// Set up contentType String
		String contentType = "";

		
		String bucketName = "bms-customer-document";
		

		try {

			// Get the uploaded file and decode from base64
			byte[] bI = Base64.decodeBase64(event.getBody().getBytes());

			// Get the content-type header and extract the boundary
			Map<String, String> hps = event.getHeaders();
			if (hps != null) {
				contentType = hps.get("Content-Type");
			}
			String[] boundaryArray = contentType.split("=");

			// Transform the boundary to a byte array
			byte[] boundary = boundaryArray[1].getBytes();

			// Log the extraction for verification purposes
			logger.log(new String(bI, "UTF-8") + "\n");

			// Create a ByteArrayInputStream
			ByteArrayInputStream content = new ByteArrayInputStream(bI);

			// Create a MultipartStream to process the form-data
			MultipartStream multipartStream = new MultipartStream(content, boundary, bI.length, null);

			// Create a ByteArrayOutputStream
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			// Find first boundary in the MultipartStream
			boolean nextPart = multipartStream.skipPreamble();
			String attachedFileName = "";
			String fileContentType = "image/png";
			// Loop through each segment
			while (nextPart) {
				String header = multipartStream.readHeaders();

				// Log header for debugging
				logger.log("Headers:");
				logger.log(header);
				System.out.println("Printing Headers");
				if (header != null && !header.isEmpty()) {
					List<String> headerList = Arrays.asList(header.split(";"));
					for (String item : headerList) {
						if (item.contains("Content-Type")) {
							fileContentType = item.split(":")[1].trim();
							System.out.println("fileContentType-->" + fileContentType);
						}
						if (item.contains("filename")) {
							attachedFileName = item.split("=")[1].split(" ")[0].trim();
							attachedFileName = attachedFileName.replace("Content-Type:","");
							attachedFileName =attachedFileName.replace("\"", "").trim();
							System.out.println("attachedFileName-->" + attachedFileName);
							
							System.out.println("Name Finished");
						}
					}

				}

				// Write out the file to our ByteArrayOutputStream
				multipartStream.readBodyData(out);
				// Get the next part, if any
				nextPart = multipartStream.readBoundary();
			}
			
			if (!ALLOWED_MIMPE_TYPE.contains(fileContentType)) {
				String responseBody = String.format("File not supported,Allowed types are %s",ALLOWED_MIMPE_TYPE.stream().collect(Collectors.joining(",")));
				logger.log(responseBody);
				return new HttpResponse(HttpStatus.SC_BAD_REQUEST, responseBody);
			}

			// Log completion of MultipartStream processing
			logger.log("Data written to ByteStream");

			// Prepare an InputStream from the ByteArrayOutputStream
			InputStream fis = new ByteArrayInputStream(out.toByteArray());

			// Create our S3Client Object
			// AmazonS3 s3Client =
			// AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
			//s3Client.putObject(BUCKET_NAME, filename, inputStream, new ObjectMetadata());
			
			// Configure the file metadata
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(out.toByteArray().length);
			metadata.setContentType(fileContentType);
			metadata.setCacheControl("public, max-age=31536000");

			System.out.println("bucketName--->" + bucketName);
			System.out.println("fileObjKeyName-->" + attachedFileName);
			System.out.println("fileContentType--?" + fileContentType);
			System.out.println("fis" + fis);
			System.out.println("metadata" + metadata);

			// Put file into S3
			s3Client.putObject(bucketName, attachedFileName, fis, metadata);

			// Log status
			logger.log("Put object in S3");

			// Provide a response
			// response.setStatusCode(200);
			Map<String, String> responseBody = new HashMap<String, String>();
			responseBody.put("Status", "File stored in S3");
			
			response = new HttpResponse(HttpStatus.SC_OK, String.format("%s File Uploaded successfully",attachedFileName));

		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't
			// process it, so it returned an error response.
			logger.log(e.getMessage());
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			logger.log(e.getMessage());
		} catch (IOException e) {
			// Handle MultipartStream class IOException
			logger.log(e.getMessage());
		}

		logger.log(response.toString());
		return response;
	}

}
