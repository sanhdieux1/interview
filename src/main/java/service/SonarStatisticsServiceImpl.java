package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;

import models.ComponentMetrics;
import models.Components;
import models.Sonar;
import util.Constant;
import util.LinkUtil;

public class SonarStatisticsServiceImpl implements SonarStatisticsService {

    final static Logger logger = Logger.getLogger(SonarStatisticsServiceImpl.class);

    public Components getComponent(String iaName, String urlStr) {
        Components component = new Components();
        List<String> sonarKeys = new ArrayList<>();
        URL url;
        BufferedReader br = null;
        try {
            url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                String[] arr = inputLine.split(",");
                if (arr[0].equals(iaName)) {
                    component.setIaName(iaName);
                    for (int i = 1; i < arr.length; i++) {
                        sonarKeys.add(arr[i]);
                    }
                    component.setSonarKeys(sonarKeys);
                }
                continue;
            }
        } catch (MalformedURLException e) {
            logger.error("MALFORMEDURLEXCEPTION " + e);
        } catch (IOException e) {
            logger.error("IOEXCEPTION " + e);
        } catch (Exception e) {
            logger.error("EXCEPTION " + e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("COMPONENT " + component);
        return component;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Sonar> getSonarStatistic(String iaNames, String metric, String url,
            String period) {
        Map<String, Sonar> sonarMap = new TreeMap<String, Sonar>();
        Sonar sonar;
        String[] iaNameList = iaNames.split(",");
        for (int i = 0; i < iaNameList.length; i++) {
            sonar = new Sonar();
            sonar.setIaName(iaNameList[i]);
            Map<String, ComponentMetrics> componentMetricMap = new TreeMap<String, ComponentMetrics>();
            Components components = getComponent(iaNameList[i], url);
            if (components.getIaName() != null) {
                List<String> sonarKeys = components.getSonarKeys();
                Document doc;
                for (String sonarKey : sonarKeys) {
                    logger.info(String.format(Constant.LINK_GET_SONAR_STATISTIC, metric, sonarKey));
                    doc = LinkUtil.getInstance().getConnectionWithProxy(
                            String.format(Constant.LINK_GET_SONAR_STATISTIC, metric, sonarKey));
                    logger.info("GET LINK SONAR "
                            + String.format(Constant.LINK_GET_SONAR_STATISTIC, metric, sonarKey));
                    if (doc == null) {
                        continue;
                    }
                    String json = doc.body().text();
                    JSONParser parser = new JSONParser();
                    try {
                        Object object = parser.parse(json);
                        JSONArray array = (JSONArray) object;
                        if (array.size() == 0) {
                            doc = LinkUtil.getInstance().getConnectionWithProxy(
                                    String.format(Constant.LINK_GET_SONAR_STATISTIC, "", sonarKey));
                            json = doc.body().text();
                            Object object2 = parser.parse(json);
                            JSONArray array2 = (JSONArray) object2;
                            JSONObject jsonObject = (JSONObject) array2.get(0);
                            ComponentMetrics componentMetrics = new ComponentMetrics();
                            componentMetrics
                                    .setId(Integer.valueOf(jsonObject.get("id").toString()));
                            componentMetrics.setName(jsonObject.get("name").toString());
                            Map<String, String> metrics = new TreeMap<String, String>();
                            String[] arr1 = metric.split(",");
                            for (int j = 0; j < arr1.length; j++) {
                                metrics.put(arr1[j], "-");
                            }
                            componentMetrics.setMetrics(metrics);
                            componentMetricMap.put(componentMetrics.getName(), componentMetrics);
                            continue;
                        }

                        JSONObject jsonObject = (JSONObject) array.get(0);
                        ComponentMetrics componentMetrics = new ComponentMetrics();
                        componentMetrics.setId(Integer.valueOf(jsonObject.get("id").toString()));
                        componentMetrics.setName(jsonObject.get("name").toString());
                        Object object2 = jsonObject.get("msr");
                        JSONArray array2 = (JSONArray) object2;
                        List<String> arr2 = new ArrayList<>();
                        if (array2 != null) {
                            for (int j = 0; j < array2.size(); j++) {
                                jsonObject = (JSONObject) array2.get(j);
                                arr2.add((String) jsonObject.get("key"));
                            }
                        } else {
                            array2 = new JSONArray();
                        }

                        Map<String, String> metrics = new TreeMap<String, String>();
                        String[] arr1 = metric.split(",");
                        List<String> arr11 = Arrays.asList(arr1);
                        for (int j = 0; j < arr1.length; j++) {
                            if (!arr2.remove(arr11.get(j))) {
                                JSONObject obj = new JSONObject();
                                obj.put("key", arr11.get(j));
                                obj.put("frmt_val", "-");
                                obj.put("var1", "-");
                                obj.put("var2", "-");
                                obj.put("var3", "-");
                                array2.add(obj);
                            }
                        }

                        logger.info("ARRAY2 " + array2);

                        for (int j = 0; j < array2.size(); j++) {
                            jsonObject = (JSONObject) array2.get(j);
                            if (!jsonObject.get("key").equals("new_coverage")) {
                                metrics.put(jsonObject.get("key").toString(),
                                        jsonObject.get("frmt_val").toString());
                            } else {
                                String new_coverage_metric = "var1";
                                if (period != null) {
                                    if (period.equals("period1")) {
                                        new_coverage_metric = "var1";
                                    } else if (period.equals("period2")) {
                                        new_coverage_metric = "var2";
                                    } else {
                                        new_coverage_metric = "var3";
                                    }
                                }

                                double newCoverage = 0;
                                try {
                                    newCoverage = (double) Math.round(Double.valueOf(
                                            jsonObject.get(new_coverage_metric).toString()) * 100)
                                            / 100;
                                } catch (Exception e) {
                                    logger.error("PERIOD " + period + " EXCEPTION " + e);
                                }
                                String new_coverage_val = String.valueOf(newCoverage);
                                if (new_coverage_val.equals("0.0")) {
                                    new_coverage_val = "-";
                                }
                                metrics.put(jsonObject.get("key").toString(), new_coverage_val);
                            }
                            componentMetrics.setMetrics(metrics);
                        }
                        componentMetricMap.put(componentMetrics.getName(), componentMetrics);
                    } catch (ParseException e) {
                        logger.error("PARSEEXCEPTION " + e);
                    }
                }
            }
            sonar.setComponentMetrics(componentMetricMap);
            sonarMap.put(sonar.getIaName(), sonar);
        }

        return sonarMap;
    }

    @Override
    public Map<String, Object> getPeriods() {
        Map<String, Object> periods = new TreeMap<>();
        Document doc = LinkUtil.getInstance().getConnectionWithProxy(Constant.LINK_GET_JIRA_PERIODS);
        if (doc != null) {
            String json = doc.body().text();
            JSONParser parser = new JSONParser();
            try {
                Object object = parser.parse(json);
                JSONArray array = (JSONArray) object;
                JSONObject jsonObject = null;
                for (int i = 0; i < array.size(); i++) {
                    jsonObject = (JSONObject) array.get(i);
                    if (jsonObject.get("key").equals("sonar.timemachine.period1")) {
                        periods.put("period1", jsonObject.get("value"));
                    }
                    if (jsonObject.get("key").equals("sonar.timemachine.period2")) {
                        periods.put("period2", jsonObject.get("value"));
                    }
                    if (jsonObject.get("key").equals("sonar.timemachine.period3")) {
                        periods.put("period3", jsonObject.get("value"));
                    }
                }
            } catch (ParseException e) {
                logger.error("PARSEEXCEPTION " + e.getMessage());
            }
        }
        return periods;
    }
}
