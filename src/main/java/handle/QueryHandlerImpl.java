package handle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.AssigneeVO;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.ProjectVO;
import models.ProjectVersionVO;
import models.exception.MException;
import models.gadget.Gadget;
import models.main.ExecutionsVO;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class QueryHandlerImpl implements QueryHandler {
    final static Logger logger = Logger.getLogger(QueryHandlerImpl.class);
    private static final String QUERY = "project in ('%s') and assignee='%s' and executionStatus in (PASS) and cycleName in ('%s')";
    private static Set<String> cycleNameCache = new HashSet<>();
    private static Set<String> projectsCache = new HashSet<>();
    private static Map<String, Set<AssigneeVO>> assigneesCache = new HashMap<String, Set<AssigneeVO>>();
    private static ObjectMapper mapper = new ObjectMapper();
    private static final String AND = " and ";

    public QueryHandlerImpl() {
    }

    public Result getAssigneeTable(String username, String cyclename, String project,
            Context context) throws MException {
        logger.info("getAssigneeTable(" + username + "," + cyclename + "," + project + ")");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY,
                String.format(QUERY, project, username, cyclename));
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(data, ExecutionsVO.class);
        return Results.json().render(executions);
    }

    private ExecutionsVO findAllExecutionIsuee(String projectName) throws MException {
        String query = "project in ('%s')";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, projectName));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }

    public Result getListCycleName(String projectName) throws MException {
        if (cycleNameCache.isEmpty()) {
            ExecutionsVO executions = findAllExecutionIsuee(projectName);
            if (executions != null) {
                List<ExecutionIssueVO> excutions = executions.getExecutions();
                Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                cycleNameCache = excutionsStream.map(i -> i.getCycleName())
                        .collect(Collectors.toSet());
            }
        }
        return Results.json().render(cycleNameCache);
    }

    public Result getAssigneeList(String projectName) throws MException {

        if (assigneesCache.get(projectName) == null || assigneesCache.get(projectName).isEmpty()) {
            ExecutionsVO executions = findAllExecutionIsuee(projectName);
            if (executions != null) {
                List<ExecutionIssueVO> excutions = executions.getExecutions();
                Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                assigneesCache.put(projectName,
                        excutionsStream.map(new Function<ExecutionIssueVO, AssigneeVO>() {
                            @Override
                            public AssigneeVO apply(ExecutionIssueVO issueVO) {
                                AssigneeVO assigneeVO = new AssigneeVO(issueVO.getAssignee(),
                                        issueVO.getAssigneeUserName(),
                                        issueVO.getAssigneeDisplay());
                                return assigneeVO;
                            }
                        }).collect(Collectors.toSet()));
            }
        }
        return Results.json().render(assigneesCache.get(projectName));
    }

    public Result getProjectList() throws MException {
        if (projectsCache.isEmpty()) {
            String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                    PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PROJECT_PATH),
                    new HashMap<String, String>());
            List<ProjectVO> projects = JSONUtil.getInstance().convertJSONtoListObject(data, ProjectVO.class);
            projectsCache = projects.stream().map(p -> p.getName()).collect(Collectors.toSet());
        }
        return Results.json().render(projectsCache);
    }

    public Result addGadget(String typeStr, String jsonData) throws MException {
        Gadget.Type type = Gadget.Type.valueOf(typeStr);
        if (type == null) {
            throw new MException(typeStr + " is not an existing type");
        }

        return Results.json();
    }

    public void clearSession() {
        cycleNameCache = null;
    }

    @Override
    public Result getProjectVersionList(long id) throws MException {
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getInstance()
                        .getString(Constant.RESOURCE_BUNLE_PROJECT_PATH + "/" + id + "/versions"),
                new HashMap<String, String>());
        List<ProjectVersionVO> projects = JSONUtil.getInstance().convertJSONtoListObject(data, ProjectVersionVO.class);
        return Results.json().render(projects);
    }
    
    
    public JQLIssueVO findIssues(String id) throws MException {
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_ISSUE_PATH) + "/" + id,
                new HashMap<>());
        JQLIssueVO issueVO = JSONUtil.getInstance().convertJSONtoObject(data, JQLIssueVO.class);
        return issueVO;
    }

   
}
