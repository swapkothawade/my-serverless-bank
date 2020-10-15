package com.mybank.aws.repository;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mybank.aws.domain.UserLogin;
import com.mybank.aws.domain.UserProfile;

public class UserProfileDao extends AbstractBMSDao {

	private final MongoCollection<UserProfile> userProfileCollection;
	private final LoginDao loginDao;

	public UserProfileDao(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);

		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		userProfileCollection = db.getCollection("user_profile", UserProfile.class)
				.withCodecRegistry(pojoCodecRegistry);
		loginDao = new LoginDao(mongoClient, databaseName);
	}

	/**
	 * Add User in UserProfile collection. Throws exception in case of any issue
	 * while writing document.
	 * 
	 * @param userProfile
	 * @return
	 */
	public boolean addUser(UserProfile userProfile) {
		try {
			userProfileCollection.insertOne(userProfile);
			loginDao.saveUser(getUser(userProfile));
			return true;
		} catch (MongoWriteException exception) {
			exception.printStackTrace();
		}
		return false;

	}

	private UserLogin getUser(UserProfile userProfile) {
		UserLogin user = new UserLogin();
		user.setEmail(userProfile.getEmail());
		user.setPassword(userProfile.getPassword());
		user.setName(userProfile.getFirstName());
		user.setLastname(userProfile.getLastName());
		Set<String> roles = new HashSet<>();
		roles.add("USER");
		user.setRole(roles);

		return user;
	}

	public UserProfile getUserProfileByEmail(String email) {
		Bson filter = eq("email", email);
		try {
			FindIterable<UserProfile> itr = userProfileCollection.find(filter);
			return itr.iterator().hasNext() ? itr.first() : null;
		} catch (MongoException exception) {
			exception.printStackTrace();
			return null;
		}

	}

	public boolean updateProfile(UserProfile userProfile) {
		Bson filter = eq("email", userProfile.getEmail());
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", userProfile);
		try {
			userProfileCollection.updateOne(filter, updateObject);
			return true;
		} catch (MongoException exception) {
			exception.printStackTrace();
			return false;
		}

	}

	public boolean deleteProfile(String email) {
		Bson filter = eq("email", email);
		try {
			userProfileCollection.deleteOne(filter);
			return true;
		} catch (MongoException exception) {
			exception.printStackTrace();
			return false;
		}

	}

	public List<UserProfile> findAll() {
		List<UserProfile> users = new ArrayList<>();
		userProfileCollection.find().iterator().forEachRemaining(item->users.add(item));
		return users;
	}
}
