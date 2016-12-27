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
    private static final String PLUS = "+";

    private static Map<String, Set<AssigneeVO>> assigneesCache = new HashMap<String, Set<AssigneeVO>>();

    public AssigneeHandlerImpl() {
    }

    public Result getListCycleName(String projectName, Release release) throws APIException {
        return Results.json().render(AssigneeUtility.getInstance().getListCycleName(projectName, release));
    }

    public Result getAssigneeList(String projectName, Release release) throws APIException {
        if(assigneesCache.get(projectName+PLUS+release) == null || assigneesCache.get(projectName+PLUS+release).isEmpty()){
            ExecutionsVO executions = AssigneeUtility.getInstance().findAllExecutionIsueeInProject(projectName, release);
            if(executions != null && executions.getExecutions() != null){
                List<ExecutionIssueVO> excutions = executions.getExecutions();
                Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                assigneesCache.put(projectName+PLUS+release, excutionsStream.filter(e -> (e.getAssigneeUserName()!=null && !e.getAssigneeUserName().isEmpty())).map(new Function<ExecutionIssueVO, AssigneeVO>() {
                    @Override
                    public AssigneeVO apply(ExecutionIssueVO issueVO) {
                        AssigneeVO assigneeVO = new AssigneeVO(issueVO.getAssignee(), issueVO.getAssigneeUserName(), issueVO.getAssigneeDisplay());
                        return assigneeVO;
                    }
                }).collect(Collectors.toSet()));
            }
        }
        Set<AssigneeVO> assignees = assigneesCache.get(projectName);
        return Results.json().render(assignees != null ? assignees : new HashSet<>());
    }

    public JQLIssueVO findIssues(String id) throws APIException {
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_ISSUE_PATH) + "/" + id,
                new HashMap<>());
        JQLIssueVO issueVO = JSONUtil.getInstance().convertJSONtoObject(data, JQLIssueVO.class);
        return issueVO;
    }

}
