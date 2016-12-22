package controllers;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.EpicHandler;
import handle.EpicHandlerImpl;
import models.exception.MException;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class EpicController {
    final static Logger logger = Logger.getLogger(EpicController.class);
    private EpicHandler handler;

    public EpicController() {
        handler = new EpicHandlerImpl();
//        handler = (EpicHandler) Proxy.newProxyInstance(EpicHandler.class.getClassLoader(),
//                new Class[] { EpicHandler.class }, new ExceptionHandler(handler));
    }

    public Result getEpicLinks(@Param("project") String project, @Param("release") String release) {
        try{
            return handler.getEpicLinks(project, release);
        } catch (MException e){
            return handleException(e);
        }
    }

    public Result findAllIssues(@Param("epic") String epic) {
        try{
            return handler.findAllIssues(epic);
        } catch (MException e){
            return handleException(e);
        }
    }

    public Result findExecutionIssues(@Param("issueKey") String issueKey) {
        try{
            return handler.findExecutionIsuee(issueKey);
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
