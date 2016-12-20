package controllers;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.EpicHandler;
import handle.EpicHandlerImpl;
import handle.ExceptionHandler;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.Param;

@Singleton
@FilterWith(CrossOriginAccessControlFilter.class)
public class EpicController {
    final static Logger logger = Logger.getLogger(EpicController.class);
    private EpicHandler handler;

    public EpicController() {
        handler = new EpicHandlerImpl();
        handler = (EpicHandler) Proxy.newProxyInstance(EpicHandler.class.getClassLoader(),
                new Class[] { EpicHandler.class }, new ExceptionHandler(handler));
    }

    public Result getEpicLinks(@Param("project") String project) {
        return handler.getEpicLinks(project);
    }

    public Result findAllIssues(@Param("epic") String epic) {
        return handler.findAllIssues(epic);
    }

    public Result findExecutionIsuee(@Param("issueKey") String issueKey) {
        return handler.findExecutionIsuee(issueKey);
    }
}
