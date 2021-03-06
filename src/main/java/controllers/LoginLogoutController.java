package controllers;

import static ninja.Results.redirect;

import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;

import com.google.inject.Singleton;

import manament.log.LoggerWapper;
import models.SessionInfo;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.Session;
import service.HTTPClientUtil;
import service.RESTService;
import service.RESTServiceImpl;
import util.Constant;
import util.JSONUtil;
import util.LinkUtil;

@Singleton
public class LoginLogoutController {

	final static LoggerWapper logger = LoggerWapper.getLogger(LoginLogoutController.class);
	
    public Result login(Context context) {
        return Results.html();
    }

    public Result loginPost(@Param("username") String username,
                            @Param("password") String password,
                            @Param("rememberMe") Boolean rememberMe,
                            Context context) {
    	LinkUtil util = LinkUtil.getInstance();
        boolean isUserNameAndPasswordValid = util.isUserAndPasswordValid(username, password);

        if (isUserNameAndPasswordValid) {
            Session session = context.getSession();
            
            Map<String, String> cookiesMap = HTTPClientUtil.getInstance().loginGreenhopper(username, password);
            if(cookiesMap != null && !cookiesMap.isEmpty()){
                SessionInfo sessionInfo = new SessionInfo();
                sessionInfo.setCookies(cookiesMap);
                String sessionInfoStr = JSONUtil.getInstance().convertToString(sessionInfo);
                session.put(Constant.API_SESSION_INFO, sessionInfoStr);
            }
            
    		session.put("username", username);
    		Document doc = util.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, username), Constant.TOKEN);
    		if (doc != null) {
    			String json = doc.body().text();
        		JSONParser parser = new JSONParser();
        		try {
        			JSONObject jsonObject = (JSONObject) parser.parse(json);
        			session.put("alias", jsonObject.get("displayName").toString());
        		} catch (ParseException e) {
        			logger.fastDebug(String.format("GET_USER_INFO OF %s ERROR ", username, e));
        		}
            } else {
            	session.put("alias", username);
            }
            
            RESTService service = new RESTServiceImpl();
            Set<Object> set = service.getUsersOfGroup("jira-administrators");
            if (set.contains(username)) {
            	session.put("role", "jira-administrators");
            }

            if (rememberMe != null && rememberMe) {
                session.setExpiryTime(Constant.EXPIRE_TIME);
            }

            context.getFlashScope().success("Login Successful");

            return Results.redirect("/");

        } else {
            context.getFlashScope().put("username", username);
            context.getFlashScope().put("rememberMe", rememberMe);
            context.getFlashScope().error("Error Login");

            return redirect("/login");
        }
    }

    public Result logout(Context context) {
        context.getSession().clear();
        context.getFlashScope().success("Logout Successful");
        
        return redirect("/");
    }
}
