package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.ExceptionHandler;
import handle.QueryHandler;
import handle.QueryHandlerImpl;
import models.exception.MException;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class AssigneeController {
	final static Logger logger = Logger.getLogger(AssigneeController.class);
    private QueryHandler handler;
    
    public AssigneeController() {
        handler = new QueryHandlerImpl();
        handler = (QueryHandler) Proxy.newProxyInstance(QueryHandler.class.getClassLoader(), new Class[]{QueryHandler.class}, new ExceptionHandler(handler));
    }
    
    public Result getAssigneeList(@Param("project") String projectName){
    	logger.info("getAssigneeList:"+ projectName);
    	try{
            return handler.getAssigneeList(projectName);
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
