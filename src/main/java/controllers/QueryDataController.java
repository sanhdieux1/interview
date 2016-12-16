package controllers;

import java.lang.reflect.Proxy;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import handle.ExceptionHandler;
import handle.MHandler;
import handle.QueryDataHandler;
import ninja.Context;
import ninja.Result;
import ninja.params.Param;

@Singleton
public class QueryDataController {
    
    private MHandler handler;
    
    public QueryDataController() {
        handler = new QueryDataHandler(); 
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
    
    public Result addGadget(String type, String jsonData){
        return handler.addGadget(type, jsonData);
    }
}
