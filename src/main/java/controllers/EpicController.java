package controllers;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.EpicHandler;
import handle.EpicHandlerImpl;
import manament.log.LoggerWapper;
import models.exception.APIException;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class EpicController {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicController.class);
    private EpicHandler handler;

    public EpicController() {
        handler = new EpicHandlerImpl();
//        handler = (EpicHandler) Proxy.newProxyInstance(EpicHandler.class.getClassLoader(),
//                new Class[] { EpicHandler.class }, new ExceptionHandler(handler));
    }

    public Result getEpicLinks(@Param("project") String project, @Param("release") String release) {
        try{
            return handler.getEpicLinks(project, release);
        } catch (APIException e){
            return handleException(e);
        }
    }

    public Result findAllIssues(@Param("epic") String epic) {
        try{
            return handler.findAllIssues(epic);
        } catch (APIException e){
            return handleException(e);
        }
    }

    public Result findExecutionIssues(@Param("issueKey") String issueKey) {
        try{
            return handler.findExecutionIsuee(issueKey);
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
