package handle;

import models.main.ExecutionsVO;
import ninja.Result;

public interface EpicHandler {
    public Result getEpicLinks(String project);
    public Result findAllIssues(String epic);
    public Result findExecutionIsuee(String issueKey);
    public Result findAllExecutionIssues(String epic);
    public ExecutionsVO findAllExecutionIsuee2(String issueKey);
}
