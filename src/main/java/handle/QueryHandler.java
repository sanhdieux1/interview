package handle;

import models.exception.APIException;
import ninja.Context;
import ninja.Result;

public interface QueryHandler {
    public Result getAssigneeTable(String username, String cyclename, String project, Context context) throws APIException;
    public Result getListCycleName(String projectName, String release) throws APIException;
    public Result getProjectList() throws APIException;
    public Result addGadget(String type, String jsonData) throws APIException;
	public Result getAssigneeList(String projectName) throws APIException;
	
}
