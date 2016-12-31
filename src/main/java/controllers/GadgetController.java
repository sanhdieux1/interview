package controllers;

import java.util.List;

import com.google.inject.Singleton;

import handle.GadgetHandler;
import handle.GadgetHandlerImpl;
import models.exception.APIException;
import models.exception.APIExceptionUtil;
import ninja.Context;
import ninja.Result;
import ninja.params.Param;
import util.JSONUtil;

@Singleton
public class GadgetController {

    private GadgetHandler handler;

    public GadgetController() {
        handler = new GadgetHandlerImpl();
    }

    public Result insertOrUpdateGadget(@Param("type") String type, @Param("data") String data, Context context) {
        try{
            return handler.insertOrUpdateGadget(type, data, context);
        } catch (APIException e){
            return APIExceptionUtil.convert(e);
        }
    }

    public Result getGadgets(@Param("dashboardId") String id) {
        try{
            return handler.getGadgets(id);
        } catch (APIException e){
            return APIExceptionUtil.convert(e);
        }

    }

    public Result getDataGadget(@Param("id") String id) {
        try{
            return handler.getDataGadget(id);
        } catch (APIException e){
            return APIExceptionUtil.convert(e);
        }
    }
    public Result getStoryInEpic(@Param("epics") String epic){
        try{
            List<String> epics = JSONUtil.getInstance().convertJSONtoListObject(epic, String.class);
            return handler.getStoryInEpic(epics);
        } catch (APIException e){
            return APIExceptionUtil.convert(e);
        }
    }

    
    public Result getProjectList(){
        try{
            return handler.getProjectList();
        } catch (APIException e){
            return APIExceptionUtil.convert(e);
        }
    }
}
