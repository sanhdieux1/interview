package util.gadget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import ch.qos.logback.classic.spi.ThrowableProxyVO;
import manament.log.LoggerWapper;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueVO;
import models.exception.APIErrorCode;
import models.exception.APIException;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.StoryVsTestExecution;
import models.main.JQLSearchResult;
import service.DatabaseUtility;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class GadgetUtility extends DatabaseUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetUtility.class);
    private static GadgetUtility INSTANCE = new GadgetUtility();

    protected DBCollection collection;

    private GadgetUtility() {
        super();
        collection = db.getCollection(Gadget.class.getSimpleName());
    }

    public static GadgetUtility getInstance() {
        return INSTANCE;
    }

    public void insertOrUpdate(Gadget gadget) throws APIException {
        try {
            DBObject dbObject = (DBObject) JSON.parse(mapper.writeValueAsString(gadget));
            dbObject.removeField("id");

            String id = gadget.getId();
            Gadget existingGadget = null;
            if (id != null) {
                existingGadget = get(id);
            }
            if (existingGadget != null) {
                BasicDBObject updateQuery = new BasicDBObject();
                updateQuery.append("$set", dbObject);
                BasicDBObject searchQuery = new BasicDBObject();
                searchQuery.append("_id", new ObjectId(id));
                collection.update(searchQuery, updateQuery);
            } else {
                collection.insert(dbObject);
            }
        } catch (JsonProcessingException e) {
            logger.fastDebug("error during mapper.writeValueAsString", e);
            throw new APIException("cannot insert gadget", e);
        }
    }

    public static void main(String[] args) throws APIException {
        StoryVsTestExecution gadget = new StoryVsTestExecution();
        Map<String, Set<String>> stories = new HashMap<>();
        
//        new JQLIssueVO
        stories.put("FNMS-96",Arrays.asList("FNMS-1483", "FNMS-1484", "FNMS-1490", "FNMS-650").stream().collect(Collectors.toSet()));
        gadget.setId("58609eec8dbec753e43964fd");
        gadget.setSelectAll(true);
        gadget.setStories(stories);
        gadget.setProjectName("FNMS-557x");
        gadget.setEpic(Arrays.asList("FNMS-96").stream().collect(Collectors.toSet()));
        GadgetUtility.getInstance().insertOrUpdate(gadget);
    }

    public Gadget get(String gadgetId) throws APIException {
        Gadget gadget = null;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(gadgetId));
        DBObject dbObj = collection.findOne(query);
        if (dbObj != null && Gadget.Type.valueOf(((String) dbObj.get("type"))) != null) {
            Gadget.Type type = Gadget.Type.valueOf(((String) dbObj.get("type")));
            try {
                if (type == Gadget.Type.EPIC_US_TEST_EXECUTION) {
                    EpicVsTestExecution epicGadget = mapper.readValue(dbObj.toString(),
                            EpicVsTestExecution.class);
                    epicGadget.setId(getObjectId(dbObj));
                    gadget = epicGadget;

                } else if (type == Gadget.Type.ASSIGNEE_TEST_EXECUTION) {

                } else if (type == Gadget.Type.TEST_CYCLE_TEST_EXECUTION) {

                } else if (type == Gadget.Type.STORY_TEST_EXECUTION) {
                    StoryVsTestExecution storyGadget = mapper.readValue(dbObj.toString(),
                            StoryVsTestExecution.class);
                    storyGadget.setId(getObjectId(dbObj));
                    gadget = storyGadget;
                }
            } catch (IOException e) {
                logger.fastDebug("Error during loading gadget", e);
                throw new APIException("Error during loading gadget", e);
            }
        }
        return gadget;
    }

    public List<Gadget> getAll() throws APIException {
        DBCursor dbCursor = collection.find();
        List<Gadget> gadgets = new ArrayList<Gadget>();
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            if (Gadget.Type.ASSIGNEE_TEST_EXECUTION
                    .equals(Gadget.Type.valueOf((String) dbObject.get("type")))) {

            } else if (Gadget.Type.EPIC_US_TEST_EXECUTION
                    .equals(Gadget.Type.valueOf((String) dbObject.get("type")))) {
                EpicVsTestExecution epicGadget = JSONUtil.getInstance()
                        .convertJSONtoObject(dbObject.toString(), EpicVsTestExecution.class);
                epicGadget.setId(getObjectId(dbObject));
                gadgets.add(epicGadget);
            } else if (Gadget.Type.TEST_CYCLE_TEST_EXECUTION
                    .equals(Gadget.Type.valueOf((String) dbObject.get("type")))) {

            }
        }
        return gadgets;
    }

    public GadgetData convertToGadgetData(ExecutionIssueResultWapper wapper) {
        GadgetData gadgetData = new GadgetData();
        if (wapper != null && wapper.getExecutionsVO() != null) {
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

    public JQLIssueVO findIssue(String issueKey) throws APIException {
        JQLSearchResult searchResult = null;
        String query = "issue=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = HTTPClientUtil.getInstance().getLegacyData(
                PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_SEARCH_PATH),
                parameters);
        try{
            searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        } catch (APIException e){
            //ignore exception, issue not found.
            if(!APIErrorCode.PARSE_JSON.equals(e.getErrorCode())){
                return null;
            } else{
                throw e;
            }
        }
        return searchResult.getIssues().get(0);
    }
}
