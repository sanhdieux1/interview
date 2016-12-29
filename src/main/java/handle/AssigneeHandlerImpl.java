package handle;

import java.util.HashMap;
import java.util.Set;

import manament.log.LoggerWapper;
import models.AssigneeVO;
import models.JQLIssueVO;
import models.exception.APIException;
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

    public Result getListCycleName(String projectName, Release release, Set<String> products) throws APIException {
        return Results.json().render(AssigneeUtility.getInstance().getListCycleName(projectName, release, products));
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
