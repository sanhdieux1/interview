package controllers;

import java.util.List;

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
import util.JSONUtil;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class EpicController {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicController.class);
    private EpicHandler handler;

    public EpicController() {
        handler = new EpicHandlerImpl();
    }

    public Result getEpicLinks(@Param("project") String project, @Param("release") String release, @Param("products") String productArrays) {
        try{
            List<String> products = JSONUtil.getInstance().convertJSONtoListObject(productArrays, String.class);
            return handler.getEpicLinks(project, release, products);
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
