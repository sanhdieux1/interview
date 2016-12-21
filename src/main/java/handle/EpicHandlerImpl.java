package handle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;
import models.main.JQLSearchResult;
import ninja.Result;
import ninja.Results;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;
import util.PropertiesUtil;

public class EpicHandlerImpl implements EpicHandler {
    final static Logger logger = Logger.getLogger(EpicHandlerImpl.class);

    @Override
    public Result getEpicLinks(String project) {
        Set<String> result = null;
        String query = "project = \"%s\" and type = epic";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, project));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data,
                JQLSearchResult.class);
        if (searchResult != null) {
            result = searchResult.getIssues().stream().map(t -> t.getKey())
                    .collect(Collectors.toSet());
        }
        return Results.json().render(result);
    }

    @Override
    public Result findAllIssues(String epic) {
        return Results.json().render(findAllIssues2(epic));
    }

    private List<JQLIssueVO> findAllIssues2(String epic) {
        String query = "\"Epic Link\"=%s";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
        parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
        JQLSearchResult searchResult = JSONUtil.getInstance().convertJSONtoObject(data,
                JQLSearchResult.class);
        return searchResult.getIssues();
    }

    public ExecutionsVO findAllExecutionIsuee2(String issueKey) {
        String query = "issue=\"%s\"";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, issueKey));
        parameters.put(Constant.PARAMERTER_MAXRECORDS, "1000");
        parameters.put(Constant.PARAMERTER_OFFSET, "0");
        String result = LinkUtil.getInstance().getLegacyDataWithProxy(
                PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = JSONUtil.getInstance().convertJSONtoObject(result,
                ExecutionsVO.class);
        return executions;
    }

    @Override
    public Result findExecutionIsuee(String issueKey) {
        return Results.json().render(findAllExecutionIsuee2(issueKey));
    }

    private List<ExecutionIssueVO> findAllTestExecutionIssue(String epic) {
        List<ExecutionIssueVO> executionIssueVOs = Collections.synchronizedList(new ArrayList<>());
        List<JQLIssueVO> issuesRefer = findAllIssues2(epic);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(issuesRefer.size());
        List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();
        EpicHandler handler = this;
        issuesRefer.stream()
                .filter(i -> i.getFields().getIssuetype().getName()
                        .equalsIgnoreCase(JQLIssuetypeVO.Type.TEST.toString()))
                .forEach(new Consumer<JQLIssueVO>() {
                    @Override
                    public void accept(JQLIssueVO issue) {
                        tasks.add(new ExecutionCallable(handler, issue.getKey()));
                    }
                });
        List<Future<ExecutionsVO>> results;
        try {
            results = taskExecutor.invokeAll(tasks);
            results.forEach(new Consumer<Future<ExecutionsVO>>() {
                @Override
                public void accept(Future<ExecutionsVO> t) {
                    ExecutionsVO executionsVO = null;
                    try {
                        executionsVO = t.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new MException("error during execute task");
                    }
                    if (executionsVO != null) {
                        executionIssueVOs.addAll(executionsVO.getExecutions());
                    }
                }
            });
        } catch (InterruptedException e) {
            logger.error("can't execute thread", e);
            throw new MException("Timeout exeption");
        }
        return executionIssueVOs;
    }

    private void findData() {

    }

    @Override
    public Result findAllExecutionIssues(String epic) {
        List<ExecutionIssueVO> executionIssueVOs = findAllTestExecutionIssue(epic);
        return Results.json().render(executionIssueVOs);
    }

    private void getDataPic(EpicVsTestExecution settings) {
        List<String> epics = settings.getEpic();
        List<String> metrics = settings.getMetrics();
        List<ExecutionIssueVO> executionIssueVOs = Collections
                .synchronizedList(new ArrayList<ExecutionIssueVO>());
        ExecutorService taskExecutor = Executors.newFixedThreadPool(epics.size());

        epics.forEach(new Consumer<String>() {
            @Override
            public void accept(String epic) {
                executionIssueVOs.addAll(findAllTestExecutionIssue(epic));
            }
        });
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("can't await thread", e);
            throw new MException("Timeout exeption");
        }
    }
}
