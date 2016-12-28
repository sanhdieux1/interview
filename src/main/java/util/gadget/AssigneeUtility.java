package util.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.AssigneeVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.exception.APIException;
import models.gadget.AssigneeVsTestExecution;
import models.main.ExecutionsVO;
import models.main.Release;
import ninja.Results;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class AssigneeUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeUtility.class);
    private static AssigneeUtility INSTANCE = new AssigneeUtility();
    private static Map<String, Set<String>> cycleNameCache = new HashMap<String, Set<String>>();;
    private static Map<String, Set<AssigneeVO>> assigneesCache = new HashMap<String, Set<AssigneeVO>>();
    private static final String PLUS = "+";

    private AssigneeUtility() {
    }

    public static AssigneeUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, List<GadgetData>> getDataAssignee(AssigneeVsTestExecution assigneeGadget) throws APIException {
        Map<String, List<GadgetData>> returnData = new HashMap<>();

        String projectName = assigneeGadget.getProjectName();
        Set<AssigneeVO> assigneeVOs = findAssigneeList(projectName, assigneeGadget.getRelease());
        Set<String> assignees = assigneeVOs.stream().map(a -> a.getDisplay()).collect(Collectors.toSet());
        Set<String> cycles = assigneeGadget.getCycles();

        if(assigneeGadget.isSelectAllTestCycle()){
            cycles = getListCycleName(projectName, assigneeGadget.getRelease());
        }
        if(cycles != null && !cycles.isEmpty()){
            for (String cycle : cycles){
                ExecutionsVO executions = findExecution(projectName, cycle, assignees);
                if(executions != null && executions.getExecutions() != null){
                    Map<String, List<ExecutionIssueVO>> assigneeMap = executions.getExecutions().stream()
                            .collect(Collectors.groupingBy(ExecutionIssueVO::getAssigneeDisplay));
                    List<GadgetData> gadgetDatas = new ArrayList<GadgetData>();
                    for (String assignee : assigneeMap.keySet()){
                        GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(assigneeMap.get(assignee));
                        gadgetData.setKey(new APIIssueVO(assignee, null));
                        gadgetDatas.add(gadgetData);
                    }
                    // init empty for assignees that have no test
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
        }else{
            logger.fastDebug("No Test Cycle in gadget %s",assigneeGadget.getId());
        }
        return returnData;
    }

    public Set<AssigneeVO> findAssigneeList(String projectName, Release release) throws APIException{
        if(assigneesCache.get(projectName+PLUS+release) == null || assigneesCache.get(projectName+PLUS+release).isEmpty()){
            ExecutionsVO executions = findAllExecutionIsueeInProject(projectName, release);
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
        Set<AssigneeVO> assignees = assigneesCache.get(projectName+PLUS+release);
        return assignees != null ? assignees : new HashSet<>();
    
    }
    public ExecutionsVO findExecution(String project, String cyclename, Set<String> assignees) throws APIException {
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
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(data, ExecutionsVO.class);
        return executions;
    }

    public ExecutionsVO findAllExecutionIsueeInProject(String projectName, Release release) throws APIException {
        StringBuffer query = new StringBuffer();
        if(projectName == null || projectName.isEmpty()){
            return null;
        }
        query.append("project = \""+projectName+"\"");
        if(release != null){
            query.append(Constant.AND);
            query.append(String.format("fixVersion = %s", release.toString()));
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, query.toString());
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }

    public Set<String> getListCycleName(String projectName, Release release) throws APIException {
        String keyProvisional = projectName + PLUS + release.toString();
        if(cycleNameCache.get(keyProvisional) == null || cycleNameCache.get(keyProvisional).isEmpty()){
            ExecutionsVO executions = AssigneeUtility.getInstance().findAllExecutionIsueeInProject(projectName, release);
            if(executions != null && executions.getExecutions()!=null){
                List<ExecutionIssueVO> excutions = executions.getExecutions();
                Stream<ExecutionIssueVO> excutionsStream = excutions.stream();
                cycleNameCache.put(projectName + PLUS + release.toString(), excutionsStream.map(i -> i.getCycleName()).collect(Collectors.toSet()));
            }
        }
        return cycleNameCache.get(keyProvisional);
    }

    public void clearSession() {
        cycleNameCache = null;
    }
}
