package service;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;

import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import util.Constant;
import util.PropertiesUtil;

public class GadgetServiceImpl {
	final static Logger logger = Logger.getLogger(GadgetServiceImpl.class);
	private MongoClient mongoClient = new MongoClient();
	private DB db = mongoClient.getDB(PropertiesUtil.getInstance().getString(Constant.DATABASE_SCHEMA));
	private ObjectMapper mapper = new ObjectMapper();
	private DBCollection collection = db.getCollection(Gadget.class.getSimpleName());

	public void insert(Gadget gadget) {
		DBObject dbObject;
		try {
			dbObject = (DBObject) JSON.parse(mapper.writeValueAsString(gadget));
		
		dbObject.removeField("id");
		WriteResult result = collection.insert(dbObject);
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
		Gadget.Type type = Gadget.Type.valueOf(((String)dbObj.get("type")));
		if (type != null) {
			if(type == Gadget.Type.EPIC_US_TEST_EXECUTION){
				try {
					EpicVsTestExecution epicGadget = mapper.readValue(dbObj.toString(), EpicVsTestExecution.class);
					ObjectId id = (ObjectId)dbObj.get( "_id" );
					epicGadget.setId((String) id.toString());
					gadget = epicGadget;
				} catch (IOException e) {
					logger.error("cannot query database", e);
					throw new MException("cannot query database");
				}
			}else if(type == Gadget.Type.ASSIGNEE_TEST_EXECUTION){
				
			}else if(type == Gadget.Type.TEST_CYCLE_TEST_EXECUTION){
				
			}
		}
		return gadget;
	}

	public static void main(String[] args) {
		GadgetServiceImpl i = new GadgetServiceImpl();
//		EpicVsTestExecution gadget = new EpicVsTestExecution();
//		gadget.setMetrics(Arrays.asList("PASSED"));
//		gadget.setRelease("1.2.01");
//		gadget.setEpic(Arrays.asList("FNMS-96"));
//		gadget.setProjectName("FMNS-557x");
//		try {
//			i.insert(gadget);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Gadget g = i.get("585a3cc08dbec732644e0ecb");
		System.out.println(g.getType());
	}
}
