package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.ExceptionHandler;
import handle.MHandler;
import handle.QueryDataHandler;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class AssigneeController {
	final static Logger logger = Logger.getLogger(AssigneeController.class);
    private MHandler handler;
    
    public AssigneeController() {
        handler = QueryDataHandler.getInstance(); 
        handler = (MHandler) Proxy.newProxyInstance(MHandler.class.getClassLoader(), new Class[]{MHandler.class}, new ExceptionHandler(handler));
    }
    
    public Result getAssigneeList(@Param("project") String projectName){
    	logger.info("getAssigneeList:"+ projectName);
    	return handler.getAssigneeList(projectName);
    }
}
