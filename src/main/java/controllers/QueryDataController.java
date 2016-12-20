package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.ExceptionHandler;
import handle.MHandler;
import handle.QueryDataHandler;
import models.ExecutionsVO;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class QueryDataController {
    final static Logger logger = Logger.getLogger(QueryDataController.class);
    private MHandler handler;
    
    public QueryDataController() {
        handler = QueryDataHandler.getInstance(); 
        handler = (MHandler) Proxy.newProxyInstance(MHandler.class.getClassLoader(), new Class[]{MHandler.class}, new ExceptionHandler(handler));
    }
    public Result getAssigneeTable(@Param("username") String username, @Param("cyclename") String cyclename, @Param("project") String project,
            Context context) {
        return handler.getAssigneeTable(username, cyclename, project, context);
    }
    public Result getListCycleName(@Param("project") String projectName) {
        return handler.getListCycleName(projectName);
    }
    
    public Result getProjectList(){
        return handler.getProjectList();
    }
    
    public Result addGadget(@Param("type") String type, @Param("gadgetData") String gadgetData){
        logger.info(gadgetData);
        return handler.addGadget(type, gadgetData);
    }
    
    public Result getProjectVersionList(@Param("projectId") long id){
        return handler.getProjectVersionList(id);
    }
    
    public Result findEpicLinks(@Param("projectName") String projectName, @Param("release") String release){
    	return handler.findEpicLinks(projectName, release);
    }
    
    public Result getEpicLinks(@Param("project")String project){
    	return handler.getEpicLinks(project);
    }
    public Result findAllIssues(@Param("epic")String epic){
    	return handler.findAllIssues(epic);
    }
    public Result findExecutionIsuee(@Param("issueKey") String issueKey) {
    	return handler.findExecutionIsuee(issueKey);
    }
}
