package handle;

import models.exception.APIException;
import models.main.Release;
import ninja.Result;

public interface AssigneeHandler {
    public Result getListCycleName(String projectName, Release release) throws APIException;
	public Result getAssigneeList(String projectName, Release release) throws APIException;
	
}
