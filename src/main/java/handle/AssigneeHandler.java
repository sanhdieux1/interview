package handle;

import java.util.Set;

import models.exception.APIException;
import models.main.Release;
import ninja.Result;

public interface AssigneeHandler {
    public Result getListCycleName(String projectName, Release release,  Set<String> products) throws APIException;
	public Result getAssigneeList(String projectName, Release release) throws APIException;
	
}
