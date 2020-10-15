package com.mybank.aws.repository;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mybank.aws.domain.UserLogin;

public class LoginDao extends AbstractBMSDao {

	private final MongoCollection<UserLogin> usersLoginCollection;

	public LoginDao(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		usersLoginCollection = db.getCollection("user_login_detail", UserLogin.class).withCodecRegistry(pojoCodecRegistry);

	}

	/**
	 * Returns the User object matching the an email string value.
	 *
	 * @param email
	 *            - email string to be matched.
	 * @return User object or null.
	 */
	public UserLogin getUser(String email) {
		UserLogin user = null;
		user = usersLoginCollection.find(eq("email", email), UserLogin.class).first();
		return user;
	}

	public boolean saveUser(UserLogin user){
		usersLoginCollection.insertOne(user);
		return true;
	}

}

