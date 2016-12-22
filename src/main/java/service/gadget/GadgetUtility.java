package service.gadget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import service.DatabaseUtility;
import util.JSONUtil;

public class GadgetUtility extends DatabaseUtility {
	final static Logger logger = Logger.getLogger(GadgetUtility.class);
	protected DBCollection collection;

	public GadgetUtility() {
		super();
		collection = db.getCollection(Gadget.class.getSimpleName());
	}

	public void insert(Gadget gadget) {
		DBObject dbObject;
		try {
			dbObject = (DBObject) JSON.parse(mapper.writeValueAsString(gadget));
			dbObject.removeField("id");
			WriteResult result = collection.insert(dbObject);
			System.out.println(result);
		} catch (JsonProcessingException e) {
			logger.error(e);
			throw new MException("cannot insert gadget");
		}
	}

	public Gadget get(String gadgetId) {
		Gadget gadget = null;
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(gadgetId));
		DBObject dbObj = collection.findOne(query);
		Gadget.Type type = Gadget.Type.valueOf(((String) dbObj.get("type")));
		if (type != null) {
			if (type == Gadget.Type.EPIC_US_TEST_EXECUTION) {
				try {
					EpicVsTestExecution epicGadget = mapper.readValue(dbObj.toString(), EpicVsTestExecution.class);
					epicGadget.setId(getObjectId(dbObj));
					gadget = epicGadget;
				} catch (IOException e) {
					logger.error("cannot query database", e);
					throw new MException("cannot query database");
				}
			} else if (type == Gadget.Type.ASSIGNEE_TEST_EXECUTION) {

			} else if (type == Gadget.Type.TEST_CYCLE_TEST_EXECUTION) {

			}
		}
		return gadget;
	}

	public List<Gadget> getAll() {
		DBCursor dbCursor = collection.find();
		List<Gadget> gadgets = new ArrayList<Gadget>();
		while(dbCursor.hasNext()){
			DBObject dbObject = dbCursor.next();
			if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get("type")))){
				
			}else if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get("type")))){
				EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), EpicVsTestExecution.class);
				epicGadget.setId(getObjectId(dbObject));
				gadgets.add(epicGadget);
			}else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get("type")))){
				
			}
		}
		return gadgets;
	}
	
	
}
