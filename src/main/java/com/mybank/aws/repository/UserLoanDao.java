package com.mybank.aws.repository;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mybank.aws.domain.Loan;

public class UserLoanDao extends AbstractBMSDao {

	private final MongoCollection<Loan> loanCollection;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public UserLoanDao(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		loanCollection = db.getCollection("user_loan", Loan.class).withCodecRegistry(pojoCodecRegistry);

	}

	public String save(Loan loanRequest) {
		String loanid = String.valueOf(generateAccountNumber());
		loanRequest.setLoanid(loanid);
		try {
			loanCollection.insertOne(loanRequest);
			return loanid;
		} catch (MongoWriteException exception) {
			logger.error("Exception occured while saving loan {}", exception.getMessage());
			return null;
		}

	}

	public List<Loan> getLoanDetailsbyUserName(String username) {
		Bson filter = eq("username", username);
		Iterator<Loan> itr = loanCollection.find(filter).iterator();
		List<Loan> loans = new ArrayList<>();
		while (itr.hasNext()) {
			loans.add(itr.next());
		}
		return loans;

	}

	public Loan getLoanDetailsbyLoanId(String loanid) {
		Bson filter = eq("loanid", loanid);
		Loan loan = loanCollection.find(filter).first();
		return loan;

	}

	private long generateAccountNumber() {
		Random r = new Random(System.currentTimeMillis());
		return 1000000000 + r.nextInt(2000000000) & Integer.MAX_VALUE;
	}

	/**
	 * 
	 * @param loanid
	 * @param status
	 * @return
	 */
	public boolean updateLoanStatus(String loanid, String status) {
		Bson filter = eq("loanid", loanid);
		Loan loan = loanCollection.find(filter).first();
		loan.setStatus(status);
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", loan);
		try {
			loanCollection.updateOne(filter, updateObject);
			return true;
		} catch (MongoException exception) {
			exception.printStackTrace();
			return false;
		}

	}

}
