package handle;

import models.exception.APIException;
import ninja.Context;
import ninja.Result;

public interface AssigneeHandler {
    public Result getListCycleName(String projectName, String release) throws APIException;
	public Result getAssigneeList(String projectName) throws APIException;
	
}
