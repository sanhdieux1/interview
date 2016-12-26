package handle;

import java.util.Set;
import java.util.stream.Collectors;

import manament.log.LoggerWapper;
import models.exception.APIException;
import ninja.Result;
import ninja.Results;

public class EpicHandlerImpl extends EpicHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicHandlerImpl.class);
    

    @Override
    public Result getEpicLinks(String project, String release) throws APIException {
        Set<String> result = epicService.getEpicLinks(project, release).stream().map(e -> e.getKey()).collect(Collectors.toSet());
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
