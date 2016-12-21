package handle;

import java.util.concurrent.Callable;

import models.main.ExecutionsVO;

public class ExecutionCallable implements Callable<ExecutionsVO> {
    private EpicHandler handler;
    private String issueKey;
    public ExecutionCallable(EpicHandler handler, String issueKey) {
        super();
        this.handler = handler;
        this.issueKey = issueKey;
    }

    @Override
    public ExecutionsVO call() throws Exception {
        return handler.findAllExecutionIsuee2(issueKey);
    }

}
