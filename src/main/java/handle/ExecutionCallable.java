package handle;

import java.util.List;
import java.util.concurrent.Callable;

import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO.Type;
import models.main.ExecutionsVO;
import service.EpicService;
import service.EpicServiceImpl;

public class ExecutionCallable implements Callable<List<ExecutionIssueVO>> {
    private EpicService handler;
    private JQLIssueVO issue;
    private Type type;
    public ExecutionCallable(EpicService handler, JQLIssueVO issue, Type type) {
        super();
        this.handler = handler;
        this.issue = issue;
        this.type = type;
    }

    @Override
    public List<ExecutionIssueVO> call() throws Exception {
        if(type.equals(Type.TEST)){
        return handler.findExecutionIsuee(issue.getKey()).getExecutions();
        }else {
            return handler.findAllExecutionIsueeInStory(issue);
        }
    }

}
