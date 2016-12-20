package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.ExceptionHandler;
import handle.QueryHandler;
import handle.QueryHandlerImpl;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
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
    
}
