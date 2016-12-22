package handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import models.ExecutionIssueVO;
import models.exception.MException;
import models.main.JQLSearchResult;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class EpicHandlerImpl extends EpicHandler {
    final static Logger logger = Logger.getLogger(EpicHandlerImpl.class);

    @Override
    public Result getEpicLinks(String project, String release) throws MException {
        Set<String> result = null;
        String query = "project = \"%s\" and type = epic and fixVersion=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, project, release));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data,
                JQLSearchResult.class);
        if (searchResult != null) {
            result = searchResult.getIssues().stream().map(t -> t.getKey())
                    .collect(Collectors.toSet());
        }
        return Results.json().render(result);
    }

    @Override
    public Result findAllIssues(String epic) throws MException {
        return Results.json().render(epicService.findAllIssuesInEpicLink(epic));
    }

    @Override
    public Result findExecutionIsuee(String issueKey) throws MException {
        return Results.json().render(epicService.findTestExecutionInIsuee(issueKey));
    }

}
