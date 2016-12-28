package handle.executors;

import java.util.List;
import java.util.concurrent.Callable;

import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO.Type;
import util.gadget.EpicUtility;
import util.gadget.StoryUtility;

public class TestExecutionCallable implements Callable<ExecutionIssueResultWapper> {
    final static LoggerWapper logger = LoggerWapper.getLogger(TestExecutionCallable.class);
    private JQLIssueVO issue;
    private Type type;

    public TestExecutionCallable(JQLIssueVO issue, Type type) {
        super();
        this.issue = issue;
        this.type = type;
    }

    @Override
    public ExecutionIssueResultWapper call() throws Exception {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
        resultWapper.setPlanned(issue.getFields().getCustomfield_14809());
        resultWapper.setIssue(new APIIssueVO(issue.getKey(), issue.getSelf()));
        if (type.equals(Type.TEST)) {
            List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance()
                    .findTestExecutionInIsuee(issue.getKey());
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
