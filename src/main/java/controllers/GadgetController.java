package controllers;

import java.util.List;

import com.google.inject.Singleton;

import filter.APIFilter;
import handle.GadgetHandler;
import handle.GadgetHandlerImpl;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;
import util.JSONUtil;

@Singleton
@FilterWith(APIFilter.class)
public class GadgetController {

    private GadgetHandler handler;

    public GadgetController() {
        handler = new GadgetHandlerImpl();
    }

    public Result insertOrUpdateGadget(@Param("type") String type, @Param("data") String data, Context context) {
        try{
            return handler.insertOrUpdateGadget(type, data, context);
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
    
    public Result deleteGadget(@Param("id") String id){
        try{
            return handler.deleteGadget(id);
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
    public Result getGadgets(@Param("dashboardId") String id) {
        try{
            return handler.getGadgets(id);
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }

    }

    public Result getDataGadget(@Param("id") String id, Context context) {
        try{
            return handler.getDataGadget(id, ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
    public Result getStoryInEpic(@Param("epics") String epic, Context context){
        try{
            List<String> epics = JSONUtil.getInstance().convertJSONtoListObject(epic, String.class);
            return handler.getStoryInEpic(epics, ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }

    
    public Result getProjectList(Context context){
        try{
            return handler.getProjectList(ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
}
