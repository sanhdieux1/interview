package handle;

import ninja.Context;
import ninja.Result;

public interface QueryHandler {
    public Result getAssigneeTable(String username, String cyclename, String project, Context context);
    public Result getListCycleName(String projectName);
    public Result getProjectList();
    public Result addGadget(String type, String jsonData);
    public Result getProjectVersionList(long id);
	
	public Result getAssigneeList(String projectName);
	
}
