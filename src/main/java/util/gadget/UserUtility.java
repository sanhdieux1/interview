package util.gadget;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import manament.log.LoggerWapper;
import models.UserVO;
import models.exception.APIException;
import service.DatabaseUtility;
import util.JSONUtil;

public class UserUtility extends DatabaseUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetUtility.class);
    public static UserUtility INSTANCE = new UserUtility();
    protected DBCollection collection;

    private UserUtility() {
        super();
        collection = db.getCollection(UserVO.class.getSimpleName());
    }

    public static UserUtility getInstance() {
        return INSTANCE;
    }

    public UserVO find(String username, String friendlyName) throws APIException {
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
            if (result.getN() != 0) {
                userVO.setId(((ObjectId) result.getUpsertedId()).toString());
            }
        }
        return userVO;
    }

    public WriteResult insert(UserVO user) throws APIException {
        try {
            DBObject dbObject = (DBObject) JSON.parse(mapper.writeValueAsString(user));
            dbObject.removeField("id");

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.append("username", user.getUsername());

            return collection.update(searchQuery, dbObject, true, false);
        } catch (JsonProcessingException e) {
            logger.fastDebug("Error during parse JSON", e);
            throw new APIException("cannot insert gadget", e);
        }
    }

}
