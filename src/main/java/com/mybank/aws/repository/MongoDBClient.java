package com.mybank.aws.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDBClient {
	private static MongoClient MONGO_CLIENT = null;
	
	public static MongoClient instantiateMongoClient(String connectionString){
		if(MONGO_CLIENT == null)
			MONGO_CLIENT= MongoClients.create(connectionString);
		return MONGO_CLIENT;
	}

}
