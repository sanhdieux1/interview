package service;

import java.util.ArrayList;
import java.util.Collections;
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
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;
import models.main.JQLSearchResult;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class EpicServiceImpl implements EpicService {
    final static Logger logger = Logger.getLogger(EpicServiceImpl.class);
    @Override
    public List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget) {
        List<String> epics = epicGadget.getEpic();
        List<String> metrics = epicGadget.getMetrics();
        List<GadgetData> result = new ArrayList<>();
        
        epics.forEach(new Consumer<String>() {
            @Override
            public void accept(String epic) {
                GadgetData gadgetData = new GadgetData();
                result.add(gadgetData);
                gadgetData.setTitle(epic);
                List<ExecutionIssueVO> executionIssues = findAllTestExecutionIssue(epic);
                executionIssues.forEach(new Consumer<ExecutionIssueVO>() {
                    @Override
                    public void accept(ExecutionIssueVO issue) {
                        switch (issue.getStatus().getName()) {
                        case "PASS":
                            gadgetData.setPassed(gadgetData.getPassed()+1);
                            break;
                        case "FAIL":
                            gadgetData.setFailed(gadgetData.getFailed()+1);
                            break;
                        case "UNEXECUTED":

                            break;
                        case "WIP":

                            break;
                        default:
                            break;
                        }
                    }
                    
                });
            }
        });
        
        return result;
    }

    public List<ExecutionIssueVO> findAllTestExecutionIssue(String epic) {
        List<ExecutionIssueVO> executionIssueVOs = Collections.synchronizedList(new ArrayList<>());
        List<JQLIssueVO> issuesRefer = findAllIssues(epic);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(issuesRefer.size());
        List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();
        EpicService handler = this;
        issuesRefer.stream().filter(i -> i.getFields().getIssuetype().getName().equalsIgnoreCase(JQLIssuetypeVO.Type.TEST.toString()))
                .forEach(new Consumer<JQLIssueVO>() {
                    @Override
                    public void accept(JQLIssueVO issue) {
                        tasks.add(new ExecutionCallable(handler, issue.getKey()));
                    }
                });
        List<Future<ExecutionsVO>> results;
        try{
            results = taskExecutor.invokeAll(tasks);
            results.forEach(new Consumer<Future<ExecutionsVO>>() {
                @Override
                public void accept(Future<ExecutionsVO> t) {
                    ExecutionsVO executionsVO = null;
                    try{
                        executionsVO = t.get();
                    } catch (InterruptedException | ExecutionException e){
                        throw new MException("error during execute task");
                    }
                    if(executionsVO != null){
                        executionIssueVOs.addAll(executionsVO.getExecutions());
                    }
                }
            });
        } catch (InterruptedException e){
            logger.error("can't execute thread", e);
            throw new MException("Timeout exeption");
        }
        return executionIssueVOs;
    }

    public ExecutionsVO findAllExecutionIsuee2(String issueKey) {
        String query = "issue=\"%s\"";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "1000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result, ExecutionsVO.class);
        return executions;
    }
    @Override
    public List<JQLIssueVO> findAllIssues(String epic) {
        String query = "\"Epic Link\"=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        return searchResult.getIssues();
    }
}
