package controllers;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.AssigneeHandler;
import handle.AssigneeHandlerImpl;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.main.Release;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.Params;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class AssigneeController {
	final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeController.class);
    private AssigneeHandler handler;
    
    public AssigneeController() {
        handler = new AssigneeHandlerImpl();
    }
    
    public Result getAssigneeList(@Param("project") String projectName, @Param("release") String release){
    	logger.fasttrace("getAssigneeList(%s,%s)", projectName, release);
    	try{
            return handler.getAssigneeList(projectName, Release.fromString(release));
        } catch (APIException e){
            return handleException(e);
        }
    }
    public Result getListCycleName(@Param("project") String projectName, @Param("release") String release, @Params("products")String[] productArrays) {
        try{
            Set<String> products =null;
            if(productArrays != null){
                products = new HashSet<String>(Arrays.asList(productArrays));
            }
            return handler.getListCycleName(projectName, Release.fromString(release), products);
        } catch (APIException e){
            return handleException(e);
        }
    }
    public Result handleException(APIException e){
        Result result = Results.json();
        result.render("type", "error");
        result.render("data", e.getMessage());
        return result;
    }
}
