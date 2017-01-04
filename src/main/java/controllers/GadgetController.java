package controllers;

import java.util.List;

import com.google.inject.Singleton;

import filter.APIFilter;
import handle.GadgetHandler;
import handle.GadgetHandlerImpl;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.exception.ResultsUtil;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;
import util.Constant;
import util.JSONUtil;

@Singleton
@FilterWith(APIFilter.class)
public class GadgetController {
    final static LoggerWapper logger = LoggerWapper.getLogger(GadgetController.class);
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
    
    public Result deleteGadget(@Param("id") String id, Context context){
        try{
            logger.fasttrace("deleteGadget(%s) , by user:%s", id, context.getSession().get(Constant.USERNAME));
            return handler.deleteGadget(id);
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
    public Result getGadgetsInDashboardId(@Param("dashboardId") String id) {
        logger.fasttrace("getGadgetsInDashboardId(%s)", id);
        try{
            return handler.getGadgets(id);
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }

    }

    public Result getDataGadget(@Param("id") String id, Context context) {
        logger.fasttrace("getDataGadget(%s)", id);
        try{
            return handler.getDataGadget(id, ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
    public Result getStoryInEpic(@Param("epics") String epic, Context context){
        logger.fasttrace("getStoryInEpic(%s)", epic);
        try{
            List<String> epics = JSONUtil.getInstance().convertJSONtoListObject(epic, String.class);
            return handler.getStoryInEpic(epics, ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }

    
    public Result getProjectList(Context context){
        logger.fasttrace("getProjectList()");
        try{
            return handler.getProjectList(ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }
}
