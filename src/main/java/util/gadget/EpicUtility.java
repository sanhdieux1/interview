package util.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import handle.executors.TestExecutionCallable;
import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.GadgetData;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.exception.APIException;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;
import models.main.JQLSearchResult;
import ninja.Results;
import service.HTTPClientUtil;
import util.Constant;
import util.JSONUtil;
import util.PropertiesUtil;

public class EpicUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(EpicUtility.class);
    private static EpicUtility INSTANCE = new EpicUtility();

    private EpicUtility() {

    }

    public static EpicUtility getInstance() {
        return INSTANCE;
    }

    public List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget) throws APIException {
        List<GadgetData> result = new ArrayList<>();
        Set<String> epics = epicGadget.getEpic();
        if(epicGadget.isSelectAll()){
            Set<APIIssueVO> epicLinks = getEpicLinks(epicGadget.getProjectName(), epicGadget.getRelease().toString());
            if(epicLinks != null){
                epics = epicLinks.stream().map(e -> e.getKey()).collect(Collectors.toSet());
            }
        }
        if(epics == null){
            return result;
        }
        List<String> metrics = epicGadget.getMetrics();

        for (String epic : epics){
            ExecutionIssueResultWapper executionIssues = findAllExecutionIssueInEpic(epic);
            GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(executionIssues.getExecutionsVO());
            gadgetData.setKey(new APIIssueVO(epic, null));
            gadgetData
                    .setUnplanned(gadgetData.getBlocked() + gadgetData.getFailed() + gadgetData.getPassed() + gadgetData.getUnexecuted() + gadgetData.getWip());
            gadgetData.setPlanned(executionIssues.getPlanned());
            result.add(gadgetData);
        }

        return result;
    }

    public ExecutionIssueResultWapper findAllExecutionIssueInEpic(String epic) throws APIException {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
        List<JQLIssueVO> issues = findAllIssuesInEpicLink(epic);
        if(issues == null || issues.isEmpty()){
            return resultWapper;
        }
        ExecutorService taskExecutor = Executors.newFixedThreadPool(PropertiesUtil.getInt(Constant.CONCURRENT_THREAD));
        List<TestExecutionCallable> tasks = new ArrayList<TestExecutionCallable>();

        issues.stream().forEach(new Consumer<JQLIssueVO>() {
            @Override
            public void accept(JQLIssueVO issue) {
                Type type = JQLIssuetypeVO.Type.fromString(issue.getFields().getIssuetype().getName());
                // ignore other
                if(type == Type.TEST || type == Type.STORY){
                    tasks.add(new TestExecutionCallable(issue, type));
                }
            }
        });
        try{
            List<Future<ExecutionIssueResultWapper>> results = taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
            for (Future<ExecutionIssueResultWapper> result : results){
                ExecutionIssueResultWapper wapper = result.get();
                resultWapper.getExecutionsVO().addAll(wapper.getExecutionsVO());
                resultWapper.increasePland(wapper.getPlanned());
            }
        } catch (ExecutionException e){
            if(e.getCause() instanceof APIException){
                throw (APIException) e.getCause();
            }
            throw new APIException("error during invoke");
        } catch (InterruptedException e){
            logger.fastDebug("error during invoke", e);
            throw new APIException("error during invoke", e);
        }
        return resultWapper;
    }

    public ExecutionsVO findTestExecutionInIsuee(String issueKey) throws APIException {
        String query = "issue=\"%s\"";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "1000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }

    public List<JQLIssueVO> findAllIssuesInEpicLink(String epic) throws APIException {
        String query = "\"Epic Link\"=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        if(data == null){
            return null;
        }
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        return searchResult.getIssues();
    }

    public Set<APIIssueVO> getEpicLinks(String project, String release) throws APIException {
        Set<APIIssueVO> result = null;
        logger.fasttrace("getEpicLinks(%s,%s)", project, release);
        if(project == null){
            throw new APIException("project param cannot be null");
        }
        StringBuilder query = new StringBuilder();
        String projectParam = "project=\"%s\"";
        query.append(String.format(projectParam, project));
        query.append(Constant.AND);
        query.append("type = epic");
        if(release != null){
            String fixVersionParam = "fixVersion=%s";
            query.append(Constant.AND);
            query.append(String.format(fixVersionParam, release));
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, query.toString());
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = HTTPClientUtil.getInstance().getLegacyData(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        if(searchResult != null && searchResult.getIssues() != null){
            result = searchResult.getIssues().stream().map(new Function<JQLIssueVO, APIIssueVO>() {
                @Override
                public APIIssueVO apply(JQLIssueVO jQLIssue) {
                    APIIssueVO apiIssue = new APIIssueVO(jQLIssue.getKey(), jQLIssue.getSelf());
                    return apiIssue;
                }
            }).collect(Collectors.toSet());
        } else{
            throw new APIException(data);
        }
        return result;
    }

}
