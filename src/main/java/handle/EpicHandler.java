package handle;

import ninja.Result;

public abstract class EpicHandler extends Handler {
	public abstract Result getEpicLinks(String project);
	public abstract Result findAllIssues(String epic);
	public abstract Result findExecutionIsuee(String issueKey);
	public abstract Result findAllExecutionIssues(String epic);
}
