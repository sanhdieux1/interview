package handle;

import models.exception.APIException;
import ninja.Result;

public abstract class EpicHandler extends Handler {
	public abstract Result getEpicLinks(String project, String release) throws APIException;
	public abstract Result findAllIssues(String epic) throws APIException;
	public abstract Result findExecutionIsuee(String issueKey) throws APIException;
}
