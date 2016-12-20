package handle;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.AssigneeVO;
import models.ExecutionsVO;
import models.IssueVO;
import models.JQLReferIssuesVO;
import models.JQLSearchResult;
import models.MException;
import models.ProjectVO;
import models.ProjectVersionVO;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import service.Gadget;
import util.Constant;
import util.LinkUtil;
import util.PropertiesUtil;

public class QueryDataHandler implements MHandler {
	final static Logger logger = Logger.getLogger(QueryDataHandler.class);
	private static final String QUERY = "project in ('%s') and assignee='%s' and executionStatus in (PASS) and cycleName in ('%s')";
	private static Set<String> cycleNameCache = new HashSet<>();
	private static Set<String> projectsCache = new HashSet<>();
	private static Map<String, Set<AssigneeVO>> assigneesCache = new HashMap<String, Set<AssigneeVO>>();
	private static ObjectMapper mapper = new ObjectMapper();
	private static final String AND = " and ";
	private static QueryDataHandler INSTANCE = new QueryDataHandler();

	private QueryDataHandler() {

	}

	public static QueryDataHandler getInstance() {
		return INSTANCE;
	}

	public Result getAssigneeTable(String username, String cyclename, String project, Context context) {
		logger.info("getAssigneeTable(" + username + "," + cyclename + "," + project + ")");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(QUERY, project, username, cyclename));
		String data = LinkUtil.getInstance()
				.getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
		ExecutionsVO executions = convertJSONtoObject(data, ExecutionsVO.class);
		return Results.json().render(executions);
	}

	private ExecutionsVO findAllIsuee(String projectName) {
		String query = "project in ('%s')";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(Constant.PARAMERTER_ZQL_QUERY, String.format(query, projectName));
		parameters.put(Constant.PARAMERTER_MAXRECORDS, "10000");
		parameters.put(Constant.PARAMERTER_OFFSET, "0");
		String result = LinkUtil.getInstance()
				.getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PATH), parameters);
		ExecutionsVO executions = convertJSONtoObject(result, ExecutionsVO.class);
		return executions;
	}

	public Result getListCycleName(String projectName) {
		if (cycleNameCache.isEmpty()) {
			ExecutionsVO executions = findAllIsuee(projectName);
			if (executions != null) {
				List<IssueVO> excutions = executions.getExecutions();
				Stream<IssueVO> excutionsStream = excutions.stream();
				cycleNameCache = excutionsStream.map(i -> i.getCycleName()).collect(Collectors.toSet());
			}
		}
		return Results.json().render(cycleNameCache);
	}

	public Result getAssigneeList(String projectName) {

		if (assigneesCache.get(projectName) == null || assigneesCache.get(projectName).isEmpty()) {
			ExecutionsVO executions = findAllIsuee(projectName);
			if (executions != null) {
				List<IssueVO> excutions = executions.getExecutions();
				Stream<IssueVO> excutionsStream = excutions.stream();
				assigneesCache.put(projectName, excutionsStream.map(new Function<IssueVO, AssigneeVO>() {
					@Override
					public AssigneeVO apply(IssueVO issueVO) {
						AssigneeVO assigneeVO = new AssigneeVO(issueVO.getAssignee(), issueVO.getAssigneeUserName(),
								issueVO.getAssigneeDisplay());
						return assigneeVO;
					}
				}).collect(Collectors.toSet()));
			}
		}
		return Results.json().render(assigneesCache.get(projectName));
	}

	public Result getProjectList() {
		if (projectsCache.isEmpty()) {
			String data = LinkUtil.getInstance().getLegacyDataWithProxy(
					PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROJECT_PATH), new HashMap<String, String>());
			List<ProjectVO> projects = convertJSONtoListObject(data, ProjectVO.class);
			projectsCache = projects.stream().map(p -> p.getName()).collect(Collectors.toSet());
		}
		return Results.json().render(projectsCache);
	}

	public Result addGadget(String typeStr, String jsonData) {
		Gadget.Type type = Gadget.Type.valueOf(typeStr);
		if (type == null) {
			throw new MException(typeStr + " is not an existing type");
		}

		return Results.json();
	}

	private <T> T convertJSONtoObject(String json, Class<T> type) {
		T result = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(json, type);
		} catch (IOException e) {
			logger.error("cannot parse result", e);
			throw new MException("cannot parse result");
		}
		return result;
	}

	private <T> List<T> convertJSONtoListObject(String json, Class<T> t) {
		List<T> listObject;
		try {
			listObject = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, t));
		} catch (IOException e) {
			logger.error("cannot parse result", e);
			throw new MException("cannot parse result");
		}
		return listObject;
	}

	public void clearSession() {
		cycleNameCache = null;
	}

	@Override
	public Result getProjectVersionList(long id) {
		String data = LinkUtil.getInstance().getLegacyDataWithProxy(
				PropertiesUtil.getString(Constant.RESOURCE_BUNLE_PROJECT_PATH + "/" + id + "/versions"),
				new HashMap<String, String>());
		List<ProjectVersionVO> projects = convertJSONtoListObject(data, ProjectVersionVO.class);
		return Results.json().render(projects);
	}

	@Override
	public Result findEpicLinks(String projectName, String release) {
		ExecutionsVO executions = findAllIsuee(projectName);
		String[] epicLinks;
		if (executions != null) {
			List<IssueVO> excutions = executions.getExecutions();
			Stream<IssueVO> excutionsStream = excutions.stream();
			excutionsStream.filter(i -> i.getStatus().getName().equals(""));
		}
		return Results.json().render(executions);
	}

	@Override
	public Result getEpicLinks(String project) {
		Set<String> result = null;
		String query = "project = \"%s\" and type = epic";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, project));
		 parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
		 parameters.put(Constant.PARAMERTER_OFFSET, "0");
		String data = LinkUtil.getInstance()
				.getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
		JQLSearchResult searchResult = convertJSONtoObject(data, JQLSearchResult.class);
		if (searchResult != null) {
			result = searchResult.getIssues().stream().map(t -> t.getKey()).collect(Collectors.toSet());
		}
		return Results.json().render(result);
	}

	public Result findAllIssues(String epic){
		List<JQLReferIssuesVO> result = null;
		String query = "\"Epic Link\"=%s";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(Constant.PARAMERTER_JQL_QUERY, String.format(query, epic));
		 parameters.put(Constant.PARAMERTER_MAXRESULTS, "10000");
		 parameters.put(Constant.PARAMERTER_OFFSET, "0");
		String data = LinkUtil.getInstance()
				.getLegacyDataWithProxy(PropertiesUtil.getString(Constant.RESOURCE_BUNLE_SEARCH_PATH), parameters);
		JQLSearchResult searchResult = convertJSONtoObject(data, JQLSearchResult.class);
		return Results.json().render(searchResult);
	}
	
}
