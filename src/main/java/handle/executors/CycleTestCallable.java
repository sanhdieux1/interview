package handle.executors;

import java.util.concurrent.Callable;

import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.main.ExecutionsVO;
import util.gadget.AssigneeUtility;

public class CycleTestCallable implements Callable<ExecutionIssueResultWapper> {
    private String cycle;
    private String project;
    
    public CycleTestCallable(String cycle, String project) {
        this.cycle = cycle;
        this.project = project;
    }

    @Override
    public ExecutionIssueResultWapper call() throws Exception {
        ExecutionIssueResultWapper wapper = new ExecutionIssueResultWapper();
        ExecutionsVO executionsVO = AssigneeUtility.getInstance().findExecution(project, cycle, null);
        if(executionsVO != null){
            wapper.setExecutionsVO(executionsVO.getExecutions());
        }
        wapper.setIssue(new APIIssueVO(cycle, null));
        return wapper;
        
    }

}
