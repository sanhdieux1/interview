package controllers;

import java.util.List;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.EpicHandler;
import handle.EpicHandlerImpl;
import manament.log.LoggerWapper;
import models.exception.APIException;
import models.exception.APIExceptionUtil;
import ninja.FilterWith;
import ninja.Result;
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
            return APIExceptionUtil.convert(e);
        }
    }
    
}
