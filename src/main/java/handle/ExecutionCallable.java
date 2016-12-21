package handle;

import java.util.concurrent.Callable;

import models.main.ExecutionsVO;
import service.EpicService;
import service.EpicServiceImpl;

public class ExecutionCallable implements Callable<ExecutionsVO> {
    private EpicService handler;
    private String issueKey;
    public ExecutionCallable(EpicService handler, String issueKey) {
        super();
        this.handler = handler;
        this.issueKey = issueKey;
    }

    @Override
    public ExecutionsVO call() throws Exception {
        return handler.findAllExecutionIsuee2(issueKey);
    }

}
