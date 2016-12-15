package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.inject.Singleton;

import models.ExecutionsVO;
import models.IssueVO;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.Constant;
import util.LinkUtil;

@Singleton
public class QueryDataController {
    final static Logger logger = Logger.getLogger(QueryDataController.class);
    private static final String QUERY = "project in ('%s') and assignee='%s' and executionStatus in (PASS) and cycleName in ('%s')";
    private static Set<String> executionCache;

    public Result getAssigneeTable(@Param("username") String username, @Param("cyclename") String cyclename, @Param("project") String project,
            Context context) {
        logger.info("getAssigneeTable(" + username + "," + cyclename + "," + project + ")");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_QUERY, String.format(QUERY, project, username, cyclename));
        String result = LinkUtil.getInstance().getLegacyDataWithProxy(parameters);
        ExecutionsVO executions = convertJSONtoObject(result);
        return Results.json().render(executions);
    }

    public Result getListCycleName(@Param("project") String projectName) {
        if(executionCache == null){
            String query = "project in ('%s')";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constant.PARAMERTER_QUERY, String.format(query, projectName));
            String result = LinkUtil.getInstance().getLegacyDataWithProxy(parameters);
            ExecutionsVO executions = convertJSONtoObject(result);
            if(executions != null){
                List<IssueVO> excutions = executions.getExecutions();
                Stream<IssueVO> excutionsStream = excutions.stream();
                executionCache = excutionsStream.map(i -> i.getCycleName()).collect(Collectors.toSet());
            }
        }
        return Results.json().render(executionCache);
    }
    
    private ExecutionsVO convertJSONtoObject(String json) {
        ExecutionsVO executions = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            executions = mapper.readValue(json, ExecutionsVO.class);
        } catch (IOException e){
            // ignore exeption
            logger.error("cannot parse result", e);
        }
        return executions;
    }
    
    public void clearSession(){
        executionCache = null;
    }
    
    
}
