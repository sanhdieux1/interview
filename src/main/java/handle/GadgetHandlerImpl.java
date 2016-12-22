package handle;

import java.util.List;

import models.GadgetData;
import models.UserVO;
import models.gadget.EpicVsTestExecution;
import models.gadget.Gadget;
import models.gadget.Gadget.Type;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import service.gadget.GadgetUtility;
import util.JSONUtil;

public class GadgetHandlerImpl extends GadgetHandler {
    private GadgetUtility gadgetService;

    public GadgetHandlerImpl() {
        gadgetService = new GadgetUtility();
    }

    @Override
    public Result addGadget(String type, String data, Context context) {
        Gadget gadget = null;
        Type gadgetType = Gadget.Type.valueOf(type);
        if(gadgetType == null){
            return getError("type " + type + " not available");
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

        }
        if(gadget != null){
            gadgetService.insert(gadget);
        } else{
            return getError("can not map to Epic gadget");
        }

        return Results.json().render("message", "successful");
    }

    private Result getError(String messages) {
        Result result = Results.json();
        result.render("message", "error");
        result.render("data", messages);
        return result;
    }

    @Override
    public Result getGadgets() {
        List<Gadget> gadgets = gadgetService.getAll();
        return Results.json().render(gadgets);
    }

    @Override
    public Result getDataGadget(String id) {
        Gadget gadget = gadgetService.get(id);
        List<GadgetData> result = null;
        if(gadget != null){
            if(Gadget.Type.EPIC_US_TEST_EXECUTION.equals(gadget.getType())){
                EpicVsTestExecution epicGadget = (EpicVsTestExecution) gadget;
                result = epicService.getDataEPic(epicGadget);
            } else if(Gadget.Type.TEST_CYCLE_TEST_EXECUTION.equals(gadget.getType())){

            } else if(Gadget.Type.ASSIGNEE_TEST_EXECUTION.equals(gadget.getType())){

            }
        }
        return Results.json().render(result);
    }
}
