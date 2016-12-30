package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.inject.Singleton;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import filter.SecureFilter;
import models.ComponentMetrics;
import models.IA;
import models.Sonar;
import models.gadget.Gadget;
import ninja.Context;
import ninja.Cookie;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.Params;
import ninja.params.PathParam;
import ninja.params.SessionParam;
import service.ODReviewsService;
import service.ODReviewsServiceImpl;
import service.SonarStatisticsService;
import service.SonarStatisticsServiceImpl;
import util.Constant;
import util.LinkUtil;

@Singleton
public class DashboardController {

	final static Logger logger = Logger.getLogger(DashboardController.class);

//	@FilterWith(SecureFilter.class)
	public Result dashboard(@SessionParam("username") String username, @PathParam("id") Integer id) {
		if (id == null) {
			id = 1;
		}
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
		FindIterable<Document> iterable = collection.find();
		Map<String, Object> metricMap = new HashMap<>();
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				metricMap.put(document.getString("code"), document.get("name"));
			}
		});

		collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		iterable = collection.find(new Document("owner", username)).sort(new Document("_id", -1));
		List<Map<String, Object>> dashboards = new ArrayList<>();

		for (Document document : iterable) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", document.get("_id"));
			String shortname = document.getString("owner");
			org.jsoup.nodes.Document doc = LinkUtil.getInstance()
					.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, shortname), Constant.TOKEN);
			logger.info("CHANGE SHORTNAME: " + shortname + " TO ALIAS: " + (doc != null));
			if (doc != null) {
				String json = doc.body().text();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(json);
					shortname = jsonObject.get("displayName").toString();
				} catch (ParseException e) {
					logger.error(String.format("GET_USER_INFO OF %s ERROR ", shortname, e));
				}
			}

			map.put("owner", shortname);
			map.put("name", document.get("dashboard_name"));
			map.put("s_ia", document.get("sonar_ia"));
			StringBuilder sb = new StringBuilder();
			if (document.getString("sonar_metrics") != null) {
				String[] metricArr = document.getString("sonar_metrics").split(",");
				for (int i = 0; i < metricArr.length; i++) {
					sb.append(metricMap.get(metricArr[i])).append(",");

				}
			}
			map.put("metric", sb);
			map.put("r_ia", document.get("od_review_ia"));
			map.put("project", document.get("od_review_project"));
			map.put("share", document.get("share"));
			dashboards.add(map);
		}

		logger.info("DASHBOARDS " + dashboards);
		mongoClient.close();
		return Results.html().render("dashboards", dashboards).render("id", id);
	}

//	@FilterWith(SecureFilter.class)
	public Result new_dashboard() {
		return Results.html();
	}

//	@FilterWith(SecureFilter.class)
	public Result new_dashboard_post(@SessionParam("username") String username, @Param("name") String name,
			@Param("share") String share) {

		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		FindIterable<Document> iterable = collection.find().sort(new Document("_id", -1)).limit(1);
		Integer max_id = 0;
		if (iterable.first() != null) {
			max_id = iterable.first().getInteger("_id");
		}
		collection.insertOne(new Document("_id", ++max_id).append("owner", username).append("dashboard_name", name)
				.append("share", share));
		mongoClient.close();
		return Results.redirect("../dashboard/" + max_id);
	}

//	@FilterWith(SecureFilter.class)
	public Result find_dashboard() {
		return Results.html();
	}

//	@FilterWith(SecureFilter.class)
	public Result find_dashboard_post(@Param("name") String name) {
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
		FindIterable<Document> iterable = collection.find();
		Map<String, Object> metricMap = new HashMap<>();
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				metricMap.put(document.getString("code"), document.get("name"));
			}
		});

		collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		iterable = collection.find(new Document("dashboard_name", Pattern.compile(name, Pattern.CASE_INSENSITIVE))
				.append("share", "public")).sort(new Document("_id", -1));
		List<Map<String, Object>> dashboards = new ArrayList<>();

		for (Document document : iterable) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", document.get("_id"));
			String shortname = document.getString("owner");
			org.jsoup.nodes.Document doc = LinkUtil.getInstance()
					.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, shortname), Constant.TOKEN);
			logger.info("CHANGE SHORTNAME: " + shortname + " TO ALIAS: " + (doc != null));
			if (doc != null) {
				String json = doc.body().text();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(json);
					shortname = jsonObject.get("displayName").toString();
				} catch (ParseException e) {
					logger.error(String.format("GET_USER_INFO OF %s ERROR ", shortname, e));
				}
			}

			map.put("owner", shortname);
			map.put("name", document.get("dashboard_name"));
			map.put("s_ia", document.get("sonar_ia"));
			StringBuilder sb = new StringBuilder();
			if (document.getString("sonar_metrics") != null) {
				String[] metricArr = document.getString("sonar_metrics").split(",");
				for (int i = 0; i < metricArr.length; i++) {
					sb.append(metricMap.get(metricArr[i])).append(",");

				}
			}
			map.put("r_ia", document.get("od_review_ia"));
			map.put("project", document.get("od_review_project"));
			map.put("share", document.get("share"));
			dashboards.add(map);
		}

		logger.info("DASHBOARDS " + dashboards);
		mongoClient.close();
		return Results.html().render("name", name).render("dashboards", dashboards);
	}

//	@FilterWith(SecureFilter.class)
	public Result show_dashboard(@SessionParam("username") String username, @SessionParam("alias") String alias,
			@SessionParam("role") String role, @PathParam("id") Long id, Context context) {
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Release");
		FindIterable<Document> iterable = collection.find();
		Set<String> releases = new TreeSet<>();
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				releases.add(document.getString("name"));
			}
		});

		collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		iterable = collection.find(new Document("_id", id));

		if (iterable.first() != null) {
			if (iterable.first().get("sonar_ia") != null) {
				logger.info("WRITE SONAR COOKIE FOR DASHBOARD_ID=" + id);
				context.addCookie(new Cookie("sonar", "open", null, context.getHostname().split(":")[0],
						Constant.MAX_AGE, "/dashboard/" + id, false, false));
			}

			if (iterable.first().get("od_review_ia") != null) {
				logger.info("WRITE REVIEW COOKIE FOR DASHBOARD_ID=" + id);
				context.addCookie(new Cookie("review", "open", null, context.getHostname().split(":")[0],
						Constant.MAX_AGE, "/dashboard/" + id, false, false));
			}
		}

		Map<String, Object> dashboard = new HashMap<>();
		String iaNames = "";
		String metric = "";
		String r_ia = "";
		String proj = "";
		String release = "";
		String period = "";

		for (Document document : iterable) {
			release = document.getString("release");
			iaNames = document.getString("sonar_ia");
			metric = document.getString("sonar_metrics");
			r_ia = document.getString("od_review_ia");
			proj = document.getString("od_review_project");
			period = document.getString("period");
			dashboard.put("id", document.get("_id"));
			String shortname = document.getString("owner");

			org.jsoup.nodes.Document doc = LinkUtil.getInstance()
					.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, shortname), Constant.TOKEN);
			logger.info("CHANGE SHORTNAME: " + shortname + " TO ALIAS: " + (doc != null));
			if (doc != null) {
				String json = doc.body().text();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(json);
					shortname = jsonObject.get("displayName").toString();
				} catch (ParseException e) {
					logger.error(String.format("GET_USER_INFO OF %s ERROR ", shortname, e));
				}
			}

			dashboard.put("owner", shortname);
			dashboard.put("name", document.get("dashboard_name"));
			dashboard.put("s_ia", document.get("sonar_ia"));
			dashboard.put("metric", document.get("sonar_metrics"));
			dashboard.put("r_ia", document.get("od_review_ia"));
			dashboard.put("project", document.get("od_review_project"));
			dashboard.put("share", document.get("share"));
			dashboard.put("release", document.get("release"));
			dashboard.put("period", document.get("period"));
		}
		logger.info("DASHBOARD " + dashboard);

		collection = mongoClient.getDatabase("Interview").getCollection("Release");
		iterable = collection.find(new Document("name", release));
		String url2 = "";
		for (Document document : iterable) {
			url2 = document.getString("url");
		}

		Set<String> s_ia = new TreeSet<>();
		URL url;
		try {
			url = new URL(url2);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;

			while ((inputLine = br.readLine()) != null) {
				String[] arr = inputLine.split(",");
				s_ia.add(arr[0]);
			}
			br.close();
		} catch (MalformedURLException e) {
			logger.error("MALFORMEDURLEXCEPTION " + e);
		} catch (IOException e) {
			logger.error("IOEXCEPTION " + e);
		}
		logger.info("LIST IA OF LINK " + url2 + " IS " + s_ia);

		collection = mongoClient.getDatabase("Interview").getCollection("Sonar_Metric");
		iterable = collection.find().sort(new Document("name", 1));
		List<Map<String, Object>> metrics = new ArrayList<>();

		for (Document document : iterable) {
			Map<String, Object> map = new TreeMap<>();
			map.put("name", document.get("name"));
			map.put("code", document.get("code"));
			metrics.add(map);
		}

		logger.info("METRICS " + metrics);

		mongoClient.close();

		Map<String, List<Map<String, Object>>> s_datas = new TreeMap<>();
		List<Map<String, Object>> s_data2 = new ArrayList<>();
		if (iaNames != null) {
			SonarStatisticsService service = new SonarStatisticsServiceImpl();
			Map<String, Sonar> sonarMap = service.getSonarStatistic(iaNames, metric, url2, period);
			sonarMap.forEach((k, v) -> {
				List<Map<String, Object>> s_data = new ArrayList<>();
				Collection<ComponentMetrics> list = v.getComponentMetrics().values();
				for (ComponentMetrics cm : list) {
					Map<String, Object> map = new TreeMap<>();
					map.put("ia", v.getIaName());
					map.put("id", cm.getId());
					map.put("name", cm.getName());
					map.put("metric", cm.getMetrics());
					s_data.add(map);
				}
				s_datas.put(v.getIaName(), s_data);
			});
			logger.info("S_DATAS: " + s_datas);
			s_data2 = s_datas.entrySet().iterator().next().getValue();
			logger.info("S_DATA: " + s_data2);
		}

		List<Map<String, Object>> r_datas = new ArrayList<>();
		if (r_ia != null) {
			ODReviewsService od_service = new ODReviewsServiceImpl();
			logger.info("GETODREVIEWS OF IA=" + r_ia + " IN PROJECT=" + proj);
			List<IA> r_data = od_service.getODReviews(r_ia, proj);
			for (IA ia : r_data) {
				Map<String, Object> map = new HashMap<>();
				map.put("ia", ia.getIaName());
				map.put("less5", ia.getLessThanFive());
				map.put("more5less10", ia.getMoreThanFiveLess10());
				map.put("more10", ia.getWayTooLate());
				r_datas.add(map);
			}
		}
		logger.info("R_DATAS " + r_datas);

		SonarStatisticsService service = new SonarStatisticsServiceImpl();
		Map<String, Object> periods = service.getPeriods();
		logger.info("PERIODS " + periods);

		
		Map<String, String> gadgetType = new LinkedHashMap<>();
		gadgetType.put("epic", Gadget.Type.EPIC_US_TEST_EXECUTION.toString());
		gadgetType.put("story", Gadget.Type.STORY_TEST_EXECUTION.toString());
		gadgetType.put("cycle", Gadget.Type.TEST_CYCLE_TEST_EXECUTION.toString());
		gadgetType.put("cycle", Gadget.Type.ASSIGNEE_TEST_EXECUTION.toString());

		return Results.html().render("id", id).render("dashboard", dashboard).render("username", username)
				.render("alias", alias).render("role", role).render("s_data", s_data2).render("s_datas", s_datas)
				.render("r_datas", r_datas).render("s_ia", s_ia).render("metrics", metrics).render("releases", releases)
				.render("periods", periods)
				.render("gadgetType",gadgetType);
	}

	@FilterWith(SecureFilter.class)
	public Result update_dashboard(@SessionParam("username") String username, @SessionParam("alias") String alias,
			@PathParam("id") Long id) {
		MongoClient mongoClient = new MongoClient();

		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		FindIterable<Document> iterable = collection.find(new Document("_id", id));
		Map<String, Object> dashboard = new HashMap<>();

		for (Document document : iterable) {
			dashboard.put("id", document.get("_id"));
			String shortname = document.getString("owner");

			org.jsoup.nodes.Document doc = LinkUtil.getInstance()
					.getConnection(String.format(Constant.LINK_GET_JIRA_USER_INFO, shortname), Constant.TOKEN);
			logger.info("CHANGE SHORTNAME: " + shortname + " TO ALIAS: " + (doc != null));
			if (doc != null) {
				String json = doc.body().text();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(json);
					shortname = jsonObject.get("displayName").toString();
				} catch (ParseException e) {
					logger.error(String.format("GET_USER_INFO OF %s ERROR ", shortname, e));
				}
			}

			dashboard.put("owner", shortname);
			dashboard.put("name", document.get("dashboard_name"));
			dashboard.put("share", document.get("share"));
		}

		logger.info("DASHBOARD " + dashboard);

		mongoClient.close();
		return Results.html().render("dashboard", dashboard).render("username", username).render("alias", alias)
				.render("id", id);
	}

	@FilterWith(SecureFilter.class)
	public Result update_dashboard_post(@PathParam("id") Long id, @SessionParam("username") String username,
			@Param("name") String name, @Params("s_ia") String[] s_ia, @Params("metric") String[] metric,
			@Param("r_ia") String r_ia, @Param("project") String project, @Param("share") String share,
			@Param("release") String release, @Param("period") String period) {
		if (s_ia != null) {
			for (int i = 0; i < s_ia.length; i++) {
				s_ia[i] = s_ia[i].trim();
			}
		}
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		FindIterable<Document> iterable = collection.find(new Document("_id", id));

		String owner = "";

		for (Document document : iterable) {
			owner = document.getString("owner");
		}

		if (!username.equals(owner)) {
			mongoClient.close();
			return Results.redirect("../../dashboard/" + id);
		}

		String new_s_ia = "";
		String new_metric = "";
		if (s_ia != null) {
			new_s_ia = String.join(",", s_ia);
		}

		if (metric != null) {
			new_metric = String.join(",", metric);
		}

		if (name != null) {
			collection.updateOne(new Document("_id", id), new Document("$set", new Document("dashboard_name", name)));
		}

		if (!new_s_ia.equals("")) {
			collection.updateOne(new Document("_id", id), new Document("$set", new Document("sonar_ia", new_s_ia)));
		}

		if (!new_metric.equals("")) {
			collection.updateOne(new Document("_id", id),
					new Document("$set", new Document("sonar_metrics", new_metric)));
		}

		if (r_ia != null) {
			collection.updateOne(new Document("_id", id), new Document("$set", new Document("od_review_ia", r_ia)));
		}

		if (project != null) {
			collection.updateOne(new Document("_id", id),
					new Document("$set", new Document("od_review_project", project)));
		}

		if (share != null) {
			collection.updateOne(new Document("_id", id), new Document("$set", new Document("share", share)));
		}

		if (release != null) {
			collection.updateOne(new Document("_id", id), new Document("$set", new Document("release", release)));
		}

		if (period != null) {
			collection.updateOne(new Document("_id", id), new Document("$set", new Document("period", period)));
		}

		mongoClient.close();
		return Results.redirect("../../dashboard/" + id);
	}

	@FilterWith(SecureFilter.class)
	public Result delete_dashboard(@PathParam("id") Long id, @SessionParam("username") String username) {
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		FindIterable<Document> iterable = collection.find(new Document("_id", id));
		String owner = iterable.first().getString("owner");
		if (owner.equals(username)) {
			collection.deleteOne(new Document("_id", id));
		}
		mongoClient.close();
		return Results.redirect("../../dashboard");
	}

	@FilterWith(SecureFilter.class)
	public Result clone_dashboard(@PathParam("id") Long id, @SessionParam("username") String username) {
		MongoClient mongoClient = new MongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase("Interview").getCollection("Dashboard");
		FindIterable<Document> iterable = collection.find().sort(new Document("_id", -1)).limit(1);
		Integer max_id = 0;
		if (iterable.first() != null) {
			max_id = iterable.first().getInteger("_id");
		}
		iterable = collection.find(new Document("_id", id));
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				FindIterable<Document> iterable2 = collection.find().sort(new Document("_id", -1)).limit(1);
				if (iterable2.first() != null) {
					collection.insertOne(new Document("_id", iterable2.first().getInteger("_id") + 1)
							.append("owner", username).append("dashboard_name", document.get("dashboard_name"))
							.append("sonar_ia", document.get("sonar_ia"))
							.append("sonar_metrics", document.get("sonar_metrics"))
							.append("od_review_ia", document.get("od_review_ia"))
							.append("od_review_project", document.get("od_review_project"))
							.append("share", document.get("share")).append("release", document.get("release"))
							.append("period", document.get("period")));
				}
			}
		});

		mongoClient.close();
		return Results.redirect("../../dashboard/" + ++max_id);
	}
}
