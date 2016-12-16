package handle;

import ninja.Context;
import ninja.Result;

public interface MHandler {
    public Result getAssigneeTable(String username, String cyclename, String project, Context context);
    public Result getListCycleName(String projectName);
    public Result getProjectList();
    public Result addGadget(String type, String jsonData);
}
