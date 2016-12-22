package handle;

import java.util.List;

import models.GadgetData;
import models.UserVO;
import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import models.gadget.StoryVsTestExecution;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import service.gadget.GadgetUtility;
import util.JSONUtil;

public class GadgetHandlerImpl extends GadgetHandler {
    private GadgetUtility gadgetService;

    public GadgetHandlerImpl() {
        gadgetService = GadgetUtility.getInstance();
    }

    @Override
    public Result addGadget(String type, String data, Context context) throws MException {
        Gadget gadget = null;
        Type gadgetType = Gadget.Type.valueOf(type);
        if(gadgetType == null){
            throw new MException("type " + type + " not available");
        }
        if(data == null){
            throw new MException("data cannot be null");
        }
        String username = (String) context.getAttribute("username");
        String friendlyname = (String) context.getAttribute("alias");
        UserVO userVO = userService.find(username, friendlyname);

        if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadgetType)){
            EpicVsTestExecution epicGadget = JSONUtil.getInstance().convertJSONtoObject(data, EpicVsTestExecution.class);
            epicGadget.setUser(userVO.getUsername());
            gadget = epicGadget;
        } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadgetType)){

        } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadgetType)){

        } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(gadgetType)){
            StoryVsTestExecution storyGadget = JSONUtil.getInstance().convertJSONtoObject(data, StoryVsTestExecution.class);
            storyGadget.setUser(userVO.getUsername());
            gadget = storyGadget;
        }
        if(gadget != null){
            gadgetService.insert(gadget);
        } else{
            throw new MException("can not map to Epic gadget");
        }

        return Results.json().render("message", "successful");
    }

    @Override
    public Result getGadgets() throws MException {
        List<Gadget> gadgets = gadgetService.getAll();
        return Results.json().render(gadgets);
    }

    @Override
    public Result getDataGadget(String id) throws MException {
        List<GadgetData> gadgetsData = null;
        Gadget gadget = gadgetService.get(id);
        if(gadget != null){
            if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadget.getType())){
                EpicVsTestExecution epicGadget = (EpicVsTestExecution) gadget;
                gadgetsData = epicService.getDataEPic(epicGadget);
            } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadget.getType())){

            } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadget.getType())){

            } else if(Gadget.Type.STORY_TEST_EXECUTION.equals(gadget.getType())){
                StoryVsTestExecution storyGadget = (StoryVsTestExecution) gadget;
                storyService.getDataStory(storyGadget);
            }
        }
        Result result = Results.json();
        result.render("type", "success");
        result.render("data", gadgetsData);
        
        return result;
    }
}
