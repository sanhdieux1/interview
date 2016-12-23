package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.ExceptionHandler;
import handle.QueryHandler;
import handle.QueryHandlerImpl;
import models.exception.MException;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class QueryDataController {
    final static Logger logger = Logger.getLogger(QueryDataController.class);
    private QueryHandler handler;
    
    public QueryDataController() {
        handler = new QueryHandlerImpl();
        handler = (QueryHandler) Proxy.newProxyInstance(QueryHandler.class.getClassLoader(), new Class[]{QueryHandler.class}, new ExceptionHandler(handler));
    }
    public Result getAssigneeTable(@Param("username") String username, @Param("cyclename") String cyclename, @Param("project") String project,
            Context context) {
        try{
            return handler.getAssigneeTable(username, cyclename, project, context);
        } catch (MException e){
            return handleException(e);
        }
    }
    public Result getListCycleName(@Param("project") String projectName, @Param("release") String release) {
        try{
            return handler.getListCycleName(projectName, release);
        } catch (MException e){
            return handleException(e);
        }
    }
    
    public Result getProjectList(){
        try{
            return handler.getProjectList();
        } catch (MException e){
            return handleException(e);
        }
    }
    
    public Result addGadget(@Param("type") String type, @Param("gadgetData") String gadgetData){
        logger.info(gadgetData);
        try{
            return handler.addGadget(type, gadgetData);
        } catch (MException e){
            return handleException(e);
        }
    }
    
    public Result handleException(MException e){
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
}
