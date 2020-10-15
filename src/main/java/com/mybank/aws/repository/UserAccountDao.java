package com.mybank.aws.repository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mybank.aws.domain.Account;
import com.mybank.aws.domain.AccountTransaction;
import com.mybank.aws.domain.AccountType;
import com.mybank.aws.domain.AwsUserAccountProfile;

public class UserAccountDao extends AbstractBMSDao {

	private final MongoCollection<AccountTransaction> transactionCollection;
	private final MongoCollection<AwsUserAccountProfile> awsUserProfileCollection;

	public UserAccountDao(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		transactionCollection = db.getCollection("account_transaction", AccountTransaction.class)
				.withCodecRegistry(pojoCodecRegistry);
		awsUserProfileCollection = db.getCollection("aws_user_account_detail", AwsUserAccountProfile.class)
				.withCodecRegistry(pojoCodecRegistry);
	}

	// use of Method reference
	public List<AccountTransaction> getAccountStatment(String accountnumber) {
		Bson transactionfilter = new Document("accountnumber", accountnumber);
		Iterator<AccountTransaction> transactionItr = transactionCollection
				.find(transactionfilter, AccountTransaction.class).iterator();
		List<AccountTransaction> transactions = new ArrayList<>();
		transactionItr.forEachRemaining(transactions::add);
		return transactions;
	}

	/**
	 * Generate account number
	 * 
	 * @param userDetails
	 * @return
	 * @throws Exception
	 */
	public AwsUserAccountProfile saveUserAccountDetails(AwsUserAccountProfile userAccountDetails, String straccountType)
			throws Exception {
		Objects.requireNonNull(userAccountDetails);
		String userName = userAccountDetails.getUserName(); // this can be
															// extracted from
															// cognito

		// check if usre has already 2 accounts(CHECKING and SAVING)
		AwsUserAccountProfile accountDetails = getUserAccountDetailsbyUserName(userName);

		if (accountDetails != null && accountDetails.getAccounts() != null
				&& accountDetails.getAccounts().size() >= 2) {
			throw new Exception("User can not have more than 2 accounts, Please contact Customer Representitve ");
		} else if (accountDetails == null || Objects.isNull(accountDetails)) {
			long accountNumbertobeAssigned = generateAccountNumber();
			AccountType accountType = "saving".equalsIgnoreCase(straccountType) ? AccountType.SAVING
					: AccountType.CHECKING;
			Account account = new Account();
			account.setAccountnumber(accountNumbertobeAssigned);
			account.setAccountType(accountType);
			Set<Account> accounts = new HashSet<>();
			accounts.add(account);
			userAccountDetails.setAccounts(accounts);
			awsUserProfileCollection.insertOne(userAccountDetails);
			return userAccountDetails;

		} else {
			// One account already present,add another account in set
			String accountType = accountDetails.getAccounts().iterator().next().getAccountType().getValue();
			if (accountType.equalsIgnoreCase(straccountType)) {
				throw new Exception(String.format("User Already has  %s Accout, can not have two %s accounts",
						accountType, accountType));
			}
			long accountNumbertobeAssigned = generateAccountNumber();
			AccountType localaccountType = "saving".equalsIgnoreCase(straccountType) ? AccountType.SAVING
					: AccountType.CHECKING;
			Account account = new Account();
			account.setAccountnumber(accountNumbertobeAssigned);
			account.setAccountType(localaccountType);
			BasicDBObject updateObject = new BasicDBObject("$addToSet", new BasicDBObject("accounts", account));
			Bson filter = eq("userName", userName);
			awsUserProfileCollection.updateOne(filter, updateObject);

		}
		return null;
	}

	/**
	 * Update only contact number and address.
	 * 
	 * @param userDetails
	 * @return
	 */
	public AwsUserAccountProfile updateUserAccountDetails(AwsUserAccountProfile userAccountDetails) {

		// user name and account number should not be part of request body,
		Bson filter = eq("userName", userAccountDetails.getUserName());

		BasicDBObject updateObject = new BasicDBObject();
		// updateObject.put("$set", new
		// BasicDBObject().put("",userAccountDetails.getUserAddress());
		updateObject.put("$set", new BasicDBObject("contactNo", userAccountDetails.getContactNo()).append("userAddress",
				userAccountDetails.getUserAddress()));
		try {
			long count = awsUserProfileCollection.updateOne(filter, updateObject).getModifiedCount();
			System.out.println("Number of records modified " + count);
			return userAccountDetails;
		} catch (MongoException exception) {
			throw exception;
		}

	}

	/**
	 * user can have Zero or max 2 accounts
	 * 
	 * @param username
	 * @return
	 */
	public AwsUserAccountProfile getUserAccountDetailsbyUserName(String username) {
		Bson filter = eq("userName", username);
		return awsUserProfileCollection.find(filter).first();
		// return
		// Stream.of(awsUserProfileCollection.find(filter).cursor()).map(item->
		// item.next()).collect(Collectors.toList());

	}

	public AwsUserAccountProfile getUserAccountDetailsbyAccountNumber(long accountNumber) {
		Bson filter = in("accounts.accountnumber", accountNumber);
		AwsUserAccountProfile userProfile = awsUserProfileCollection.find(filter).first();
		return userProfile;
	}

	private long generateAccountNumber() {
		Random r = new Random(System.currentTimeMillis());
		return 1000000000 + r.nextInt(2000000000) & Integer.MAX_VALUE;
	}

}
