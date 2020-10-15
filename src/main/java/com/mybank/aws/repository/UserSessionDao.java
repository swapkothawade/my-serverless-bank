package com.mybank.aws.repository;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mybank.aws.dto.UserSessionToken;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

public class UserSessionDao extends AbstractBMSDao {
	
	private final MongoCollection<UserSessionToken> usersSessionCollection;
	

	public UserSessionDao(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		usersSessionCollection = db.getCollection("user_session", UserSessionToken.class).withCodecRegistry(pojoCodecRegistry);

	}
	
	public boolean saveUserSession(String username, String token){
        UserSessionToken userSession = new UserSessionToken(username,token);
        usersSessionCollection.insertOne(userSession);
        return true;
    }

    public boolean isUserSessionValid(String username,String token){
        FindIterable<UserSessionToken> userSession = usersSessionCollection.find(and(eq("username",username),eq("token",token)));
        if(userSession.iterator().hasNext())
            return true;
        return false;
    }


    public boolean deleteUserSession(String token){
        DeleteResult result = usersSessionCollection.deleteOne(eq("token",token));
       return  result.getDeletedCount() > 0 ?  true :  false;
    }
	
	


}
