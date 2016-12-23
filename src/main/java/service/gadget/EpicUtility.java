package service.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import handle.ExecutionCallable;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueLinkVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;
import models.main.JQLSearchResult;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class EpicUtility {
    final static Logger logger = Logger.getLogger(EpicUtility.class);
    private static EpicUtility INSTANCE = new EpicUtility();

    private EpicUtility() {

    }

    public static EpicUtility getInstance() {
        return INSTANCE;
    }

    public List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget) throws MException {
        List<String> epics = epicGadget.getEpic();
        List<String> metrics = epicGadget.getMetrics();
        List<GadgetData> result = new ArrayList<>();
        for (String epic : epics){
            ExecutionIssueResultWapper executionIssues = findAllExecutionIssueInEpic(epic);
            GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(executionIssues);
            gadgetData.setTitle(epic);
            result.add(gadgetData);
            gadgetData
                    .setUnplanned(gadgetData.getBlocked() + gadgetData.getFailed() + gadgetData.getPassed() + gadgetData.getUnexecuted() + gadgetData.getWip());
            gadgetData.setPlanned(executionIssues.getPlanned());

        }

        return result;
    }

    public ExecutionIssueResultWapper findAllExecutionIssueInEpic(String epic) throws MException {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
        List<JQLIssueVO> issues = findAllIssuesInEpicLink(epic);
        if(issues == null || issues.isEmpty()){
            return resultWapper;
        }
        ExecutorService taskExecutor = Executors.newFixedThreadPool(issues.size());
        List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();

        issues.stream().forEach(new Consumer<JQLIssueVO>() {
            @Override
            public void accept(JQLIssueVO issue) {
                Type type = JQLIssuetypeVO.Type.fromString(issue.getFields().getIssuetype().getName());
                // ignore other
                if(type == Type.TEST || type == Type.STORY){
                    tasks.add(new ExecutionCallable(issue, type));
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
            if(e.getCause() instanceof MException){
                throw (MException) e.getCause();
            }
            throw new MException("error during invoke");
        } catch (InterruptedException e){
            logger.error("error during invoke", e);
            throw new MException("error during invoke");
        }
        return resultWapper;
    }

    public ExecutionsVO findTestExecutionInIsuee(String issueKey) throws MException {
        String query = "issue=\"%s\"";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "1000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }

    public List<JQLIssueVO> findAllIssuesInEpicLink(String epic) throws MException {
        String query = "\"Epic Link\"=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        if(data == null){
            return null;
        }
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        return searchResult.getIssues();
    }

}
