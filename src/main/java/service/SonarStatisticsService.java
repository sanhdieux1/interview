package service;

import java.util.Map;

import models.Components;
import models.Sonar;

public interface SonarStatisticsService {
	Components getComponent(String iaName, String url);
	Map<String, Sonar> getSonarStatistic(String iaNames, String metric, String url, String period);
	Map<String, Object> getPeriods();
}
