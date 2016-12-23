package handle;

import models.exception.MException;
import ninja.Context;
import ninja.Result;

public interface QueryHandler {
    public Result getAssigneeTable(String username, String cyclename, String project, Context context) throws MException;
    public Result getListCycleName(String projectName, String release) throws MException;
    public Result getProjectList() throws MException;
    public Result addGadget(String type, String jsonData) throws MException;
	public Result getAssigneeList(String projectName) throws MException;
	
}
