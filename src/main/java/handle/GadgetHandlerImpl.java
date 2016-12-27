package handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import manament.log.LoggerWapper;
import models.GadgetData;
import models.JQLIssueVO;
import models.UserVO;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.gadget.CycleVsTestExecution;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import models.gadget.StoryVsTestExecution;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.JSONUtil;
import util.gadget.GadgetUtility;

public class GadgetHandlerImpl extends GadgetHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetHandlerImpl.class);
    
    public GadgetHandlerImpl() {
        gadgetService = GadgetUtility.getInstance();
    }

    @Override
    public Result addGadget(String type, String data, Context context) throws APIException {
        Gadget gadget = null;
        Type gadgetType = Gadget.Type.valueOf(type);
        if(gadgetType == null){
            throw new APIException("type " + type + " not available");
        }
        if(data == null){
            throw new APIException("data cannot be null");
        }
        String username = (String) context.getAttribute("username");
        String friendlyname = (String) context.getAttribute("alias");
        UserVO userVO = userService.find(username, friendlyname);

        if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadgetType)){
            EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(data, EpicVsTestExecution.class);
            epicGadget.setUser(userVO.getUsername());
            gadget = epicGadget;
        } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadgetType)){
            AssigneeVsTestExecution assigneeGadget = JSONUtil.getInstance().convertJSONtoObject(data, AssigneeVsTestExecution.class);
            assigneeGadget.setUser(userVO.getUsername());
            gadget = assigneeGadget;
        } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadgetType)){
            CycleVsTestExecution cycleGadget = JSONUtil.getInstance().convertJSONtoObject(data, CycleVsTestExecution.class);
            cycleGadget.setUser(userVO.getUsername());
            gadget = cycleGadget;
        } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(gadgetType)){
            StoryVsTestExecution storyGadget = JSONUtil.getInstance().convertJSONtoObject(data, StoryVsTestExecution.class);
            storyGadget.setUser(userVO.getUsername());
            gadget = storyGadget;
        }
        if(gadget != null){
            gadgetService.insertOrUpdate(gadget);
        } else{
            throw new APIException("can not map to Epic gadget");
        }

        return Results.json().render("message", "successful");
    }

    @Override
    public Result getGadgets() throws APIException {
        List<Gadget> gadgets = gadgetService.getAll();
        return Results.json().render(gadgets);
    }

    @Override
    public Result getDataGadget(String id) throws APIException {
        Map<String, List<GadgetData>> gadgetsData = new HashMap<>();;
        Gadget gadget = gadgetService.get(id);
        if(gadget != null){
            if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadget.getType())){
                EpicVsTestExecution epicGadget = (EpicVsTestExecution) gadget;
                List<GadgetData> epicData = epicService.getDataEPic(epicGadget);
                gadgetsData.put(epicGadget.getProjectName(), epicData);
            } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadget.getType())){
                CycleVsTestExecution cycleGadget = (CycleVsTestExecution) gadget;
                List<GadgetData> cycleData = cycleService.getDataCycle(cycleGadget);
                gadgetsData.put(cycleGadget.getProjectName(), cycleData);
            } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadget.getType())){
                AssigneeVsTestExecution assigneeGadget = (AssigneeVsTestExecution) gadget;
                gadgetsData = assigneeService.getDataAssignee(assigneeGadget);
            } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(gadget.getType())){
                StoryVsTestExecution storyGadget = (StoryVsTestExecution) gadget;
                gadgetsData = storyService.getDataStory(storyGadget);
            }
        }else {
            throw new APIException(String.format("gadget id=%s not found", id));
        }
        Result result = Results.json();
        result.render("type", "success");
        result.render("data", gadgetsData);
        
        return result;
    }

    @Override
    public Result getStoryInEpic(List<String> epics) throws APIException {
        Map<String, Set<JQLIssueVO>> storiesIssues = storyService.findStoryInEpic(epics);
        Map<String, Set<String>> storiesInEpic = new HashMap<>();
        storiesIssues.forEach(new BiConsumer<String, Set<JQLIssueVO>>() {
            @Override
            public void accept(String epic, Set<JQLIssueVO> storiesIssue) {
                //Filter issueKey
                storiesInEpic.put(epic, storiesIssue.stream().map(i -> i.getKey()).collect(Collectors.toSet()));
            }
        });
        
        return Results.json().render(storiesInEpic);
    }

    @Override
    public Result getProjectList() throws APIException {
        return Results.json().render(gadgetService.getProjectList());
    }
    
   
}
