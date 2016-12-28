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
import models.ProjectVO;
import models.exception.APIErrorCode;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.gadget.CycleVsTestExecution;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.StoryVsTestExecution;
import models.main.JQLSearchResult;
import models.main.Release;
import ninja.Result;
import ninja.Results;
import service.DatabaseUtility;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class GadgetUtility extends DatabaseUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetUtility.class);
    private static final String TYPE = "type";
    private static Set<String> projectsCache = new HashSet<>();
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
                logger.fasttrace("update gadget id %s by user:%s", id,  gadget.getUser());
                collection.update(searchQuery, updateQuery);
            } else {
                logger.fasttrace("insert gadget:%s by user:%s", gadget.getType(), gadget.getUser());
                collection.insert(dbObject);
            }
        } catch (JsonProcessingException e) {
            logger.fastDebug("error during mapper.writeValueAsString", e);
            throw new APIException("cannot insert gadget", e);
        }
    }

    public static void main(String[] args) throws APIException {
        AssigneeVsTestExecution gadget = new AssigneeVsTestExecution();
        gadget.setProjectName("FNMS 557x");
        Set<String> cyckes = new HashSet<>();
        cyckes.add("FNMS-5949");
        cyckes.add("FNMS-5948");
        Set<String> epic = new HashSet<>();
        epic.add("FNMS-1895");
        
        gadget.setSelectAllTestCycle(true);
        gadget.setRelease(Release.R1_2_01);
        GadgetUtility.getInstance().insertOrUpdate(gadget);
    }

    public Gadget get(String gadgetId) throws APIException {
        Gadget gadget = null;
        BasicDBObject query = new BasicDBObject();
        try{
            query.put("_id", new ObjectId(gadgetId));
        } catch (java.lang.IllegalArgumentException e){
            logger.fasttrace("gadget id %s not found", gadgetId);
            return null;
        }
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
                    AssigneeVsTestExecution assigneeGadget = mapper.readValue(dbObj.toString(),
                            AssigneeVsTestExecution.class);
                    assigneeGadget.setId(getObjectId(dbObj));
                    gadget = assigneeGadget;
                } else if (type == Gadget.Type.TEST_CYCLE_TEST_EXECUTION) {
                    CycleVsTestExecution cycleGadget = mapper.readValue(dbObj.toString(),CycleVsTestExecution.class);
                    cycleGadget.setId(getObjectId(dbObj));
                    gadget = cycleGadget;
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
        }else{
            logger.fasttrace("gadget id %s not found", gadgetId);
        }
        return gadget;
    }

    public List<Gadget> getAll() throws APIException {
        DBCursor dbCursor = collection.find();
        List<Gadget> gadgets = new ArrayList<Gadget>();
        while (dbCursor.hasNext()){
            DBObject dbObject = dbCursor.next();
            if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get(TYPE)))){
                AssigneeVsTestExecution assigneeGadget = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), AssigneeVsTestExecution.class);
                assigneeGadget.setId(getObjectId(dbObject));
                gadgets.add(assigneeGadget);
            } else if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get(TYPE)))){
                EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), EpicVsTestExecution.class);
                epicGadget.setId(getObjectId(dbObject));
                gadgets.add(epicGadget);
            } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get(TYPE)))){
                CycleVsTestExecution cyclGadget = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), CycleVsTestExecution.class);
                cyclGadget.setId(getObjectId(dbObject));
                gadgets.add(cyclGadget);
            } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(Gadget.Type.valueOf((String) dbObject.get(TYPE)))){
                StoryVsTestExecution storyGadget = JSONUtil.getInstance().convertJSONtoObject(dbObject.toString(), StoryVsTestExecution.class);
                storyGadget.setId(getObjectId(dbObject));
                gadgets.add(storyGadget);
            } else{
                logger.fastDebug("type %s is not available", dbObject.get(TYPE));
            }
        }
        return gadgets;
    }

    public GadgetData convertToGadgetData(List<ExecutionIssueVO> testExecution) {
        GadgetData gadgetData = new GadgetData();
        if (testExecution != null) {
            testExecution.forEach(new Consumer<ExecutionIssueVO>() {
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
        gadgetData.setUnplanned(gadgetData.getBlocked()+gadgetData.getFailed()+gadgetData.getPassed()+gadgetData.getWip()+gadgetData.getUnexecuted());
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
    
    public Set<String> getProjectList() throws APIException {
        if (projectsCache.isEmpty()) {
            String data = HTTPClientUtil.getInstance().getLegacyData(
                    PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PROJECT_PATH),
                    new HashMap<String, String>());
            List<ProjectVO> projects = JSONUtil.getInstance().convertJSONtoListObject(data,
                    ProjectVO.class);
            projectsCache = projects.stream().map(p -> p.getName()).collect(Collectors.toSet());
        }
        return projectsCache;
    }
}
