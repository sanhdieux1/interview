package controllers;

import java.util.List;

import com.google.inject.Singleton;

import handle.GadgetHandler;
import handle.GadgetHandlerImpl;
import models.exception.APIException;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.JSONUtil;

@Singleton
public class GadgetController {

    private GadgetHandler handler;

    public GadgetController() {
        handler = new GadgetHandlerImpl();
    }

    public Result addGadget(@Param("type") String type, @Param("data") String data, Context context) {
        try{
            return handler.addGadget(type, data, context);
        } catch (APIException e){
            return handleException(e);
        }
    }

    public Result getGadgets() {
        try{
            return handler.getGadgets();
        } catch (APIException e){
            return handleException(e);
        }

    }

    public Result getDataGadget(@Param("id") String id) {
        try{
            return handler.getDataGadget(id);
        } catch (APIException e){
            return handleException(e);
        }
    }
    public Result getStoryInEpic(@Param("epics") String epic){
        try{
            List<String> epics = JSONUtil.getInstance().convertJSONtoListObject(epic, String.class);
            return handler.getStoryInEpic(epics);
        } catch (APIException e){
            return handleException(e);
        }
    }

    public Result handleException(APIException e) {
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
    
    public Result getProjectList(){
        try{
            return handler.getProjectList();
        } catch (APIException e){
            return handleException(e);
        }
    }
}
