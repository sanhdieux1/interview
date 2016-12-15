package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Singleton;

import models.ExecutionsVO;
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
	
	public Result getAssigneeTable(@Param("username") String username, @Param("cyclename") String cyclename, @Param("project")String project, Context context) {
		logger.info("getAssigneeTable(" + username + "," + cyclename + "," + project + ")");
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(Constant.PARAMERTER_QUERY, String.format(QUERY, project,username, cyclename ));
		String result = LinkUtil.getInstance().getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST),PropertiesUtil.getString(Constant.RESOURCE_BUNLE_HOST_TYPE) ,
				PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_IP), Integer.parseInt(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_PORT)),
				PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROXY_TYPE), parameters);
		logger.info(result); 
		ExecutionsVO executions = new ExecutionsVO();
		try{
            ObjectMapper mapper = new ObjectMapper();
            executions = mapper.readValue(result, ExecutionsVO.class);
        } catch (IOException e){
            //ignore exeption
            logger.error("cannot parse result", e);
        }
		return Results.json().render(executions);
	}
}
