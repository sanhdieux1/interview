package util;

public class Constant {
	public static final int TIMEOUT = 60 * 1000;
	public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L;

	public static final int MAX_AGE = 30 * 24 * 60 * 60 * 1000;
	
	public static final String PROXY_IP = "10.10.10.10";
	public static final int PROXY_PORT = 8080;
	public static final String PROXY_TYPE = "http";
	public static final String TOKEN = "Y3J1YWRtaW46Y3J1YWRtaW4=";
	
	public static final String LINK_CRUCIBLE = "http://tiger.in.alcatel-lucent.com:8060";
//	public static final String LINK_REST_USER2 = "http://tiger.in.alcatel-lucent.com:8060/rest-service/users-v1/";
	
	public static final String LINK_GET_ODREVIEWS = "http://tiger.in.alcatel-lucent.com:8091/rest/axs-jira-plugin/1.0/ODReviews/getData/?iaName=%s&projects=%s";
	public static final String LINK_GET_COMPONENTS = "http://tiger.in.alcatel-lucent.com:8091/rest/axs-jira-plugin/1.0/configuration/iacomponents/for/__global__.json";
	public static final String LINK_GET_ODREVIEW_REPORTS = "http://tiger.in.alcatel-lucent.com:8060/rest-service/reviews-v1/filter/details?creator=%s&project=%s&states=Review";
	public static final String LINK_GET_JIRA_PERIODS = "http://bamboo.in.alcatel-lucent.com:8085/api/properties?format=json";
	public static final String LINK_GET_SONAR_STATISTIC = "http://bamboo.in.alcatel-lucent.com:8085/api/resources?format=json&metrics=%s&includetrends=true&resource=%s";
	
	public static final String LINK_GET_JIRA_USER_INFO = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/user?username=%s&expand=groups";
	public static final String LINK_GET_JIRA_PROJECTS = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/project";
	public static final String LINK_GET_JIRA_GROUPS = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/groups/picker?maxResults=10000";
	public static final String LINK_GET_JIRA_USERS_OF_GROUP = "http://tiger.in.alcatel-lucent.com:8091/rest/api/2/group?groupname=%s&expand=users";
	
	
	public static final String RESOURCE_BUNLE_HOST = "resourcebundle.host";
	public static final String RESOURCE_BUNLE_PATH = "resourcebundle.path";
	public static final String RESOURCE_BUNLE_HOST_TYPE = "resourcebundle.host.type";
	public static final String RESOURCE_BUNLE_PROXY_IP = "resourcebundle.proxy.ip";
	public static final String RESOURCE_BUNLE_PROXY_PORT = "resourcebundle.proxy.port";
	public static final String RESOURCE_BUNLE_PROXY_TYPE = "resourcebundle.proxy.type";
	public static final String PARAMERTER_QUERY = "zqlQuery";
    public static final String RESOURCE_BUNLE_PROJECT_PATH = "resourcebundle.project.path";
}
