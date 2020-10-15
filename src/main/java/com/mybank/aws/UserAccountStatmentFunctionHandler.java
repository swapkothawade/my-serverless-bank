package com.mybank.aws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.mongodb.client.MongoClient;
import com.mybank.aws.common.HttpRequest;
import com.mybank.aws.common.HttpResponse;
import com.mybank.aws.domain.AccountTransaction;
import com.mybank.aws.domain.TransactionType;
import com.mybank.aws.repository.MongoDBClient;
import com.mybank.aws.repository.UserAccountDao;

public class UserAccountStatmentFunctionHandler implements RequestHandler<HttpRequest, HttpResponse> {

	private UserAccountDao userAccountDao = null;
	private String CONN_STRING = "mongodb://root:root%402020@<dbhost>/admin?retryWrites=true&w=majority";
	private MongoClient mongoclient = null;
	private final String MY_DATABSE = "mybank";
	private final String BUCKET_NAME = "bms-customer-document";
	private final String[] HEADERS = { "Transaction Date", "Transaction Remarks", "Withdrawal Amount (INR )",
			"Deposit Amount (INR )", "Balance (INR )" };

	public UserAccountStatmentFunctionHandler() {
		mongoclient = MongoDBClient.instantiateMongoClient(CONN_STRING);
		userAccountDao = new UserAccountDao(mongoclient, MY_DATABSE);

	}

	public HttpResponse handleRequest(HttpRequest request, Context context) {
		context.getLogger().log("Input: " + request);
		String format = request.getPathParameters().get("format");
		String accountid = request.getPathParameters().get("accountid");

		List<AccountTransaction> transactions = userAccountDao.getAccountStatment(accountid);
		String fileUrl = "something went wrong";
		try {
			if (format.equalsIgnoreCase("csv")) {
				fileUrl = writeCSVToS3(transactions, format);

			} else if (format.equalsIgnoreCase("xlsx") || format.equalsIgnoreCase("xls")) {

				fileUrl = writeXlsToS3(transactions, format);

			} else {
				return new HttpResponse(HttpStatus.SC_BAD_REQUEST,
						String.format("Requested format %s not supported, Allowed formats are [csv,xlsx]", format));
			}
		} catch (Exception e) {
			return new HttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "RSomethign went wrong, please try again");

		}

		return new HttpResponse(HttpStatus.SC_OK,
				String.format("File uploaded successfully, Download url [%s]", fileUrl));
	}
	/*
	 * Should return s3 url
	 */

	public String writeXlsToS3(List<AccountTransaction> transactions, String format) throws Exception {
		// Upload file
		ByteArrayInputStream inputStream = downloadXlsReport(transactions);
		return writeToS3Bucket(inputStream, format);

	}

	/**
	 * Generate CSV file, write it to Bucket and share link
	 * 
	 * @param transactions
	 * @return
	 * @throws Exception
	 */
	public String writeCSVToS3(List<AccountTransaction> transactions, String format) throws Exception {
		ByteArrayInputStream inputStream = generateCSVReport(transactions);
		return writeToS3Bucket(inputStream, format);
	}

	private ByteArrayInputStream generateCSVReport(List<AccountTransaction> transactions) {
		StringBuffer buffer = new StringBuffer();
		String headesLine = Arrays.asList(HEADERS).stream().collect(Collectors.joining(","));
		buffer.append(headesLine).append("\n");
		for (AccountTransaction transaction : transactions) {
			StringBuffer line = new StringBuffer();
			line.append(transaction.getTransactionDateTime()).append(",").append(transaction.getTransactionRemark() == null ? "" : transaction.getTransactionRemark())
					.append(",")
					.append(transaction.getTransactionType() == TransactionType.WITHDRAWAL
							? transaction.getTransactionAmount() : "0.0")
					.append(",").append(transaction.getTransactionType() == TransactionType.DEPOSIT
							? transaction.getTransactionAmount() : "0.0").append(",")
					.append(transaction.getBalance());
			buffer.append(line).append("\n");
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.toString().getBytes());

		return byteArrayInputStream;
	}

	private String writeToS3Bucket(ByteArrayInputStream inputStream, String format) {
		String filename = String.format("AccountStatement%s.%s", LocalDateTime.now(), format);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
		s3Client.putObject(BUCKET_NAME, filename, inputStream, new ObjectMetadata());
		String s3url = "https://bms-customer-document.s3.us-east-2.amazonaws.com/" + filename;
		return s3url;
	}

	private ByteArrayInputStream downloadXlsReport(List<AccountTransaction> transactions) throws Exception {

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			CreationHelper createHelper = workbook.getCreationHelper();

			Sheet sheet = workbook.createSheet("Account Statement");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLUE.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			// Row for Header
			Row headerRow = sheet.createRow(0);

			// Header
			for (int col = 0; col < HEADERS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERS[col]);
				cell.setCellStyle(headerCellStyle);
			}

			// CellStyle for Age
			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

			int rowIdx = 1;
			for (AccountTransaction transaction : transactions) {
				Row row = sheet.createRow(rowIdx++);

				row.createCell(0).setCellValue("" + transaction.getTransactionDateTime());
				row.createCell(1).setCellValue(transaction.getTransactionRemark());
				row.createCell(2).setCellValue(transaction.getTransactionType() == TransactionType.WITHDRAWAL
						? transaction.getTransactionAmount() : 0.0);
				row.createCell(3).setCellValue(transaction.getTransactionType() == TransactionType.DEPOSIT
						? transaction.getTransactionAmount() : 0.0);
				row.createCell(4).setCellValue(transaction.getBalance());
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}
}
