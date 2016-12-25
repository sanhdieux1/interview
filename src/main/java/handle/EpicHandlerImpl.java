package handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import manament.log.LoggerWapper;
import models.ExecutionIssueVO;
import models.exception.APIException;
import models.main.ExecutionsVO;
import models.main.JQLSearchResult;
import ninja.Result;
import ninja.Results;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class EpicHandlerImpl extends EpicHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicHandlerImpl.class);
    

    @Override
    public Result getEpicLinks(String project, String release) throws APIException {
        Set<String> result = epicService.getEpicLinks(project, release);
        return Results.json().render(result);
    }

    @Override
    public Result findAllIssues(String epic) throws APIException {
        return Results.json().render(epicService.findAllIssuesInEpicLink(epic));
    }

    @Override
    public Result findExecutionIsuee(String issueKey) throws APIException {
        return Results.json().render(epicService.findTestExecutionInIsuee(issueKey));
    }

}
