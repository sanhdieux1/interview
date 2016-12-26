package util.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.main.ExecutionsVO;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class AssigneeUtility {
    private static AssigneeUtility INSTANCE = new AssigneeUtility();
    private static Set<String> cycleNameCache = new HashSet<>();

    private AssigneeUtility() {
    }

    public static AssigneeUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, List<GadgetData>> getDataAssignee(AssigneeVsTestExecution assigneeGadget) throws APIException {
        Map<String, List<GadgetData>> returnData = new HashMap<>();
        String projectName = assigneeGadget.getProjectName();
        Set<String> cycles = assigneeGadget.getCycles();
        Set<String> assignees = assigneeGadget.getAssignee();
        for (String cycle : cycles){
            ExecutionsVO executions = findExecution(projectName, cycle, assignees);
            if(executions!=null && executions.getExecutions()!=null){
                Map<String, List<ExecutionIssueVO>> assigneeMap = executions.getExecutions().stream().collect(Collectors.groupingBy(ExecutionIssueVO::getAssigneeDisplay));
                List<GadgetData> gadgetDatas = new ArrayList<GadgetData>();
                for(String assignee : assigneeMap.keySet()){
                    ExecutionIssueResultWapper issueWapper = new ExecutionIssueResultWapper();
                    issueWapper.setExecutionsVO(assigneeMap.get(assignee));
                    GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(issueWapper);
                    gadgetData.setKey(new APIIssueVO(assignee,null));
                    gadgetDatas.add(gadgetData);
                }
                //init empty for assignees that have no test
                for (String assignee : assignees){
                    if(!assigneeMap.containsKey(assignee)){
                        GadgetData gadgetData = new GadgetData();
                        gadgetData.setKey(new APIIssueVO(assignee, null));
                        gadgetDatas.add(gadgetData);
                    }
                }
                returnData.put(cycle, gadgetDatas);
            }
        }
        return returnData;
    }

    public ExecutionsVO findExecution(String project, String cyclename, Set<String> assignees) throws APIException {

        // String QUERY = "project in ('%s') and assignee='%s' and executionStatus in (PASS) and cycleName in ('%s')";
        StringBuilder query = new StringBuilder();
        query.append(String.format("project = \"%s\"", project));
        if(assignees != null && !assignees.isEmpty()){
            query.append(Constant.AND);
            query.append("(");
            boolean first = true;
            for (String assignee : assignees){
                if(!first){
                    query.append(Constant.OR);
                }
                query.append(String.format("assignee=\"%s\"", assignee));
                first = false;
            }
            query.append(")");
        }
        if(cyclename != null){
            query.append(Constant.AND);
            query.append(String.format("cycleName ~ \"%s\"", cyclename));
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, query.toString());
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(data, ExecutionsVO.class);
        return executions;
    }

    public ExecutionsVO findAllExecutionIsueeInProject(String projectName) throws APIException {
        String query = "project = %s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, projectName));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }

    public Set<String> getListCycleName(String projectName, String release) throws APIException {
        if(cycleNameCache.isEmpty()){
            ExecutionsVO executions = AssigneeUtility.getInstance().findAllExecutionIsueeInProject(projectName);
            if(executions != null){
                List<ExecutionIssueVO> excutions = executions.getExecutions();
                Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                cycleNameCache = excutionsStream.map(i -> i.getCycleName()).collect(Collectors.toSet());
            }
        }
        return cycleNameCache;
    }

    public void clearSession() {
        cycleNameCache = null;
    }
}
