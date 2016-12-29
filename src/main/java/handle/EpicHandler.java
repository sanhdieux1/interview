package handle;

import java.util.List;

import models.exception.APIException;
import ninja.Result;

public abstract class EpicHandler extends Handler {
	public abstract Result getEpicLinks(String project, String release, List<String> products) throws APIException;
	public abstract Result findAllIssues(String epic) throws APIException;
	public abstract Result findExecutionIsuee(String issueKey) throws APIException;
}
