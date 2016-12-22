package service.gadget;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import models.UserVO;
import models.exception.MException;
import service.DatabaseUtility;
import util.JSONUtil;

public class UserUtility extends DatabaseUtility {
	final static Logger logger = Logger.getLogger(GadgetUtility.class);
	public static UserUtility INSTANCE = new UserUtility();
	protected DBCollection collection;
	
	private UserUtility() {
		super();
		collection = db.getCollection(UserVO.class.getSimpleName());
	}

	public static UserUtility getInstance(){
	    return INSTANCE;
	}
	public UserVO find(String username, String friendlyName) throws MException {
		UserVO userVO = null;
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("username", username);
		DBCursor cursor = collection.find(whereQuery);
		if (cursor.count() > 0 && cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			userVO = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), UserVO.class);
			userVO.setId(getObjectId(dbObject));
		} else {
			userVO = new UserVO(username, friendlyName);
			WriteResult result = insert(userVO);
			if(result.getN() != 0){
				userVO.setId(((ObjectId)result.getUpsertedId()).toString());
			}
		}
		return userVO;
	}

	public WriteResult insert(UserVO user) throws MException {
		try {
			DBObject dbObject = (DBObject) JSON.parse(mapper.writeValueAsString(user));
			dbObject.removeField("id");
			
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.append("username", user.getUsername() );

			return collection.update(searchQuery, dbObject, true, false);
		} catch (JsonProcessingException e) {
			logger.error(e);
			throw new MException("cannot insert gadget");
		}
	}
	
}