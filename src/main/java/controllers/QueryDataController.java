package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;

import models.ExecutionsVO;
import models.IssueVO;
import models.ProjectVO;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.Constant;
import util.LinkUtil;
import util.PropertiesUtil;

@Singleton
public class QueryDataController {
    final static Logger logger = Logger.getLogger(QueryDataController.class);
    private static final String QUERY = "project in ('%s') and assignee='%s' and executionStatus in (PASS) and cycleName in ('%s')";
    private static Set<String> cycleNameCache = new HashSet<>();
    private static Set<String> projectsCache = new HashSet<>();;
    public Result getAssigneeTable(@Param("username") String username, @Param("cyclename") String cyclename, @Param("project") String project,
            Context context) {
        logger.info("getAssigneeTable(" + username + "," + cyclename + "," + project + ")");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Constant.PARAMERTER_QUERY, String.format(QUERY, project, username, cyclename));
        String data = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
        ExecutionsVO executions = convertJSONtoObject(data, ExecutionsVO.class);
        return Results.json().render(executions);
    }

    public Result getListCycleName(@Param("project") String projectName) {
        if(cycleNameCache == null){
            String query = "project in ('%s')";
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(Constant.PARAMERTER_QUERY, String.format(query, projectName));
            parameters.put(Constant.PARAMERTER_MAXRECORDS, "10000");
            parameters.put(Constant.PARAMERTER_OFFSET, "0");
            String result = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
            logger.info(result);
            ExecutionsVO executions = convertJSONtoObject(result, ExecutionsVO.class);
            if(executions != null){
                List<IssueVO> excutions = executions.getExecutions();
                Stream<IssueVO> excutionsStream = excutions.stream();
                cycleNameCache = excutionsStream.map(i -> i.getCycleName()).collect(Collectors.toSet());
            }
        }
        return Results.json().render(cycleNameCache);
    }
    
    public Result getProjectList(){
        if(projectsCache == null){
            ObjectMapper mapper = new ObjectMapper();
            String data = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROJECT_PATH), new HashMap<String, String>());
            logger.info(data);
            try{
                List<ProjectVO> projects = mapper.readValue(data, mapper.getTypeFactory().constructCollectionType(List.class, ProjectVO.class));
                projectsCache = projects.stream().map(p -> p.getName()).collect(Collectors.toSet());
            } catch (IOException e){
                // ignore exeption
                logger.error("cannot parse result", e);
            }
        }
        return Results.json().render(projectsCache);
    }
    
    private <T> T convertJSONtoObject(String json, Class<T> type) {
        T result = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(json, type);
        } catch (IOException e){
            // ignore exeption
            logger.error("cannot parse result", e);
        }
        return result;
    }
    
    public void clearSession(){
        cycleNameCache = null;
    }
    
    
}
