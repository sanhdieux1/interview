package handle;

import java.util.List;
import java.util.concurrent.Callable;

import manament.log.LoggerWapper;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO.Type;
import util.gadget.EpicUtility;
import util.gadget.StoryUtility;

public class ExecutionCallable implements Callable<ExecutionIssueResultWapper> {
    final static LoggerWapper logger = LoggerWapper.getLogger(ExecutionCallable.class);
    private JQLIssueVO issue;
    private Type type;

    public ExecutionCallable(JQLIssueVO issue, Type type) {
        super();
        this.issue = issue;
        this.type = type;
    }

    @Override
    public ExecutionIssueResultWapper call() throws Exception {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
        resultWapper.setTitle(issue.getKey());
        if (type.equals(Type.TEST)) {
            List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance()
                    .findTestExecutionInIsuee(issue.getKey()).getExecutions();
            if (executionIssues != null && !executionIssues.isEmpty()) {
                resultWapper.getExecutionsVO().addAll(executionIssues);
            }
        } else {
            List<ExecutionIssueVO> executionIssues = StoryUtility.getInstance()
                    .findAllTestExecutionInStory(issue);
            resultWapper.setPlanned(issue.getFields().getCustomfield_14809());
            if (executionIssues != null && !executionIssues.isEmpty()) {
                resultWapper.getExecutionsVO().addAll(executionIssues);
            }
        }
        return resultWapper;
    }

}
