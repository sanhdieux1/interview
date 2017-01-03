package controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Singleton;

import filter.APIFilter;
import handle.AssigneeHandler;
import handle.AssigneeHandlerImpl;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.exception.ResultsUtil;
import models.main.Release;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;
import util.JSONUtil;

@Singleton
@FilterWith(APIFilter.class)
public class AssigneeController {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeController.class);
    private AssigneeHandler handler;

    public AssigneeController() {
        handler = new AssigneeHandlerImpl();
    }

    public Result getAssigneeList(@Param("project") String projectName, @Param("release") String release, Context context) {
        logger.fasttrace("getAssigneeList(%s,%s)", projectName, release);
        try{
            return handler.getAssigneeList(projectName, Release.fromString(release), ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }

    public Result getListCycleName(@Param("project") String projectName, @Param("release") String release, @Param("products") String productArrays, Context context) {
        try{
            List<String> productList = JSONUtil.getInstance().convertJSONtoListObject(productArrays, String.class);
            Set<String> products = null;
            if(productList != null){
                products = new HashSet<>(productList);
            }
            return handler.getListCycleName(projectName, Release.fromString(release), products, ResultsUtil.getSessionInfo(context));
        } catch (APIException e){
            return ResultsUtil.convertException(e);
        }
    }

}
