package service;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import models.gadget.Gadget;
import util.Constant;
import util.PropertiesUtil;

public class DatabaseUtility {
	protected MongoClient mongoClient = new MongoClient();
	protected DB db = mongoClient.getDB(PropertiesUtil.getInstance().getString(Constant.DATABASE_SCHEMA));
	protected ObjectMapper mapper = new ObjectMapper();
	
	public String getObjectId(DBObject dbObj){
		ObjectId id = (ObjectId) dbObj.get("_id");
		return (String) id.toString();
	}

}
