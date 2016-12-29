package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import util.Constant;
import util.LinkUtil;

public class ApplicationController {

	final static Logger logger = Logger.getLogger(ApplicationController.class);
	public Result index(@PathParam("id") Integer id) {
		if (id == null) {
			id = 1;
		}
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		FindIterable<Document> iterable = collection.find(new Document("share", "public")).sort(new Document("_id", -1));
		List<Map<String, Object>> dashboards = new ArrayList<>();
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", document.get("_id"));
				String username = document.getString("owner");
				
				org.jsoup.nodes.Document doc = LinkUtil.getInstance().getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, username), Constant.TOKEN);
				logger.info("CHANGE SHORTNAME: " + username + " TO ALIAS: " + (doc != null));
	    		if (doc != null) {
	    			String json = doc.body().text();
	        		JSONParser parser = new JSONParser();
	        		try {
	        			JSONObject jsonObject = (JSONObject) parser.parse(json);
	        			username = jsonObject.get("displayName").toString();
	        		} catch (ParseException e) {
	        			logger.error(String.format("GET_USER_INFO OF %s ERROR ", username, e));
	        		}
	            }
				
				map.put("owner", username);
				map.put("name", document.get("dashboard_name"));
				dashboards.add(map);
			}
		});
		logger.info("DASHBOARDS " + dashboards);
		mongoClient.close();
		return Results.html().render("dashboards", dashboards).render("id", id);
	}
	
	
	
}
