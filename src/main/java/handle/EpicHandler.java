package handle;

import ninja.Result;

public abstract class EpicHandler extends Handler {
	public abstract Result getEpicLinks(String project, String release);
	public abstract Result findAllIssues(String epic);
	public abstract Result findExecutionIsuee(String issueKey);
}
