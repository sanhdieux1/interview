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
                ExecutionIssueResultWapper executionIssues = findAllExecutionIssueInEpic(epic);
                executionIssues.getExecutionsVO().forEach(new Consumer<ExecutionIssueVO>() {
                    @Override
                    public void accept(ExecutionIssueVO issue) {
                        switch (issue.getStatus().getName()) {
                        case "PASS":
                            gadgetData.setPassed(gadgetData.getPassed() + 1);
                            break;
                        case "FAIL":
                            gadgetData.setFailed(gadgetData.getFailed() + 1);
                            break;
                        case "UNEXECUTED":
                            gadgetData.setUnexecuted(gadgetData.getUnexecuted() + 1);
                            break;
                        case "WIP":
                            gadgetData.setWip(gadgetData.getWip() + 1);
                            break;
                        case "BLOCKED":
                            gadgetData.setBlocked(gadgetData.getBlocked() + 1);
                            break;
                        default:
                            break;
                        }
                    }
                });
                gadgetData.setUnplanned(
                        gadgetData.getBlocked() + gadgetData.getFailed() + gadgetData.getPassed() + gadgetData.getUnexecuted() + gadgetData.getWip());
                gadgetData.setPlanned(executionIssues.getPlanned());
            }
        });

        return result;
    }

    public ExecutionIssueResultWapper findAllExecutionIssueInEpic(String epic) throws MException {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper(); 
        List<JQLIssueVO> issues = findAllIssuesInEpicLink(epic);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(issues.size());
        List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();
        EpicService handler = this;

        issues.stream().forEach(new Consumer<JQLIssueVO>() {
            @Override
            public void accept(JQLIssueVO issue) {
                Type type = JQLIssuetypeVO.Type.fromString(issue.getFields().getIssuetype().getName());
                //ignore other
                if(type == Type.TEST || type == Type.STORY){
                    tasks.add(new ExecutionCallable(handler, issue, type, resultWapper));
                }
            }
        });
//        List<Future<ExecutionIssueResultWapper>> results;
        try{
            taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
//            results.forEach(new Consumer<Future<ExecutionIssueResultWapper>>() {
//                @Override
//                public void accept(Future<ExecutionIssueResultWapper> t) {
//                    ExecutionIssueResultWapper resultWapper = null;
//                    try{
//                            resultWapper = t.get();
//                    } catch (InterruptedException | ExecutionException e){
//                        logger.error("ingore", e);
//                    }
//                    if(resultWapper != null){
//                        executionIssueVOs.addAll(resultWapper.getExecutionsVO());
//                    }
//                }
//            });
        } catch (InterruptedException e){
            logger.error("can't execute thread", e);
            throw new MException("Timeout exeption");
        }
        return resultWapper;
    }

    public ExecutionsVO findTestExecutionInIsuee(String issueKey) {
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
    public List<JQLIssueVO> findAllIssuesInEpicLink(String epic) {
        String query = "\"Epic Link\"=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getInstance().getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data, JQLSearchResult.class);
        return searchResult.getIssues();
    }

    @Override
    public List<ExecutionIssueVO> findAllTestExecutionInStory(JQLIssueVO issue) {
        List<ExecutionIssueVO> result = new ArrayList<>();
        if(JQLIssuetypeVO.Type.STORY.toString().equalsIgnoreCase(issue.getFields().getIssuetype().getName())){
            issue.getFields().getIssuelinks().forEach(new Consumer<JQLIssueLinkVO>() {
                @Override
                public void accept(JQLIssueLinkVO issueLink) {
                    List<ExecutionIssueVO> executionIssues = findTestExecutionInIsuee(issueLink.getId()).getExecutions();
                    if(executionIssues != null && !executionIssues.isEmpty()){
                        result.addAll(executionIssues);
                    }
                }
            });
        }
        return result;
    }
}
