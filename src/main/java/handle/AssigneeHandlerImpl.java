package handle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import manament.log.LoggerWapper;
import models.AssigneeVO;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.exception.APIException;
import models.main.ExecutionsVO;
import models.main.Release;
import ninja.Result;
import ninja.Results;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;
import util.gadget.AssigneeUtility;

public class AssigneeHandlerImpl implements AssigneeHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeHandlerImpl.class);

    public AssigneeHandlerImpl() {
    }

    public Result getListCycleName(String projectName, Release release) throws APIException {
        return Results.json().render(AssigneeUtility.getInstance().getListCycleName(projectName, release));
    }

    public Result getAssigneeList(String projectName, Release release) throws APIException {
        Set<AssigneeVO> assignees = AssigneeUtility.getInstance().findAssigneeList(projectName, release);
        return Results.json().render(assignees);
    }

    public JQLIssueVO findIssues(String id) throws APIException {
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_ISSUE_PATH) + "/" + id,
                new HashMap<>());
        JQLIssueVO issueVO = JSONUtil.getInstance().convertJSONtoObject(data, JQLIssueVO.class);
        return issueVO;
    }

}
