package controllers;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import filter.CrossOriginAccessControlFilter;
import handle.EpicHandler;
import handle.EpicHandlerImpl;
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
//        handler = (EpicHandler) Proxy.newProxyInstance(EpicHandler.class.getClassLoader(),
//                new Class[] { EpicHandler.class }, new ExceptionHandler(handler));
    }

    public Result getEpicLinks(@Param("project") String project, @Param("release") String release) {
        return handler.getEpicLinks(project, release);
    }

    public Result findAllIssues(@Param("epic") String epic) {
        return handler.findAllIssues(epic);
    }

    public Result findExecutionIssues(@Param("issueKey") String issueKey) {
        return handler.findExecutionIsuee(issueKey);
    }
}
