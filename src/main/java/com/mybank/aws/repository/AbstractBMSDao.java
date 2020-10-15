package com.mybank.aws.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public abstract class AbstractBMSDao {
	protected final String MYBANK_DATABASE;
	protected MongoDatabase db;
	protected MongoClient mongoClient;
	
	protected AbstractBMSDao(MongoClient mongoClient, String databaseName) {
		this.mongoClient = mongoClient;
		MYBANK_DATABASE = databaseName;
		this.db = this.mongoClient.getDatabase(MYBANK_DATABASE);
	}
}
