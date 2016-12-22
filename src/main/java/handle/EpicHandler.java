package handle;

import models.exception.MException;
import ninja.Result;

public abstract class EpicHandler extends Handler {
	public abstract Result getEpicLinks(String project, String release) throws MException;
	public abstract Result findAllIssues(String epic) throws MException;
	public abstract Result findExecutionIsuee(String issueKey) throws MException;
}
