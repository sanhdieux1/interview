package service.gadget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueVO;
import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.StoryVsTestExecution;
import models.main.JQLSearchResult;
import service.DatabaseUtility;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class GadgetUtility extends DatabaseUtility {
    final static Logger logger = Logger.getLogger(GadgetUtility.class);
    private static GadgetUtility INSTANCE = new GadgetUtility();
    
    protected DBCollection collection;
    
    private GadgetUtility() {
        super();
        collection = db.getCollection(Gadget.class.getSimpleName());
    }
    public static GadgetUtility getInstance(){
        return INSTANCE;
    }
    public void insert(Gadget gadget) throws MException {
        DBObject dbObject;
        try{
            dbObject = (DBObject) JSON.parse(mapper.writeValueAsString(gadget));
            dbObject.removeField("id");
            WriteResult result = collection.insert(dbObject);
            System.out.println(result);
        } catch (JsonProcessingException e){
            logger.error(e);
            throw new MException("cannot insert gadget");
        }
    }
    public static void main(String[] args) throws MException {
        StoryVsTestExecution gadget = new StoryVsTestExecution();
        gadget.setStories(Arrays.asList("FNMS-1483", "FNMS-1484","FNMS-1490"));
        GadgetUtility.getInstance().insert(gadget);
    }
    public Gadget get(String gadgetId) throws MException {
        Gadget gadget = null;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(gadgetId));
        DBObject dbObj = collection.findOne(query);
        Gadget.Type type = Gadget.Type.valueOf(((String) dbObj.get("type")));
        if(type != null){
            try{
                if(type == Gadget.Type.EPIC_US_TEST_EXECUTION){
                    EpicVsTestExecution epicGadget = mapper.readValue(dbObj.toString(), EpicVsTestExecution.class);
                    epicGadget.setId(getObjectId(dbObj));
                    gadget = epicGadget;

                } else if(type == Gadget.Type.ASSIGNEE_TEST_EXECUTION){

                } else if(type == Gadget.Type.TEST_CYCLE_TEST_EXECUTION){

                } else if(type == Gadget.Type.STORY_TEST_EXECUTION){
                    StoryVsTestExecution storyGadget = mapper.readValue(dbObj.toString(), StoryVsTestExecution.class);
                    storyGadget.setId(getObjectId(dbObj));
                    gadget = storyGadget;
                }
            } catch (IOException e){
                logger.error("Error during loading gadget", e);
                throw new MException("Error during loading gadget");
            }
        }
        return gadget;
    }

    public List<Gadget> getAll() throws MException {
        DBCursor dbCursor = collection.find();
        List<Gadget> gadgets = new ArrayList<Gadget>();
        while (dbCursor.hasNext()){
            DBObject dbObject = dbCursor.next();
            if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get("type")))){

            } else if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get("type")))){
                EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), EpicVsTestExecution.class);
                epicGadget.setId(getObjectId(dbObject));
                gadgets.add(epicGadget);
            } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get("type")))){

            }
        }
        return gadgets;
    }

    public GadgetData convertToGadgetData(ExecutionIssueResultWapper wapper) {
        GadgetData gadgetData = new GadgetData();
        if(wapper!=null && wapper.getExecutionsVO() != null){
            wapper.getExecutionsVO().forEach(new Consumer<ExecutionIssueVO>() {
                @Override
                public void accept(ExecutionIssueVO issue) {
                    switch (issue.getStatus().getName()) {
                    case "PASS":
                        gadgetData.increasePassed(1);
                        break;
                    case "FAIL":
                        gadgetData.increaseFailed(1);
                        break;
                    case "UNEXECUTED":
                        gadgetData.increaseUnexecuted(1);
                        break;
                    case "WIP":
                        gadgetData.increaseWip(1);
                        break;
                    case "BLOCKED":
                        gadgetData.increaseBlocked(1);
                        break;
                    default:
                        break;
                    }
                }
            });
        }
        return gadgetData;
    }
    
    public JQLIssueVO findIssue(String issueKey) throws MException{
        String query = "issue=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        return searchResult.getIssues().get(0);
    }
}
