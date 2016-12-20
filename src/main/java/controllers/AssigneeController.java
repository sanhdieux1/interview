package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.ExceptionHandler;
import handle.QueryHandler;
import handle.QueryHandlerImpl;
import ninja.FilterWith;
import ninja.Result;
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
    	return handler.getAssigneeList(projectName);
    }
}
