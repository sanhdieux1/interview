package handle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO.Type;
import service.gadget.EpicUtility;
import service.gadget.StoryUtility;

public class ExecutionCallable implements Callable<Void> {
    final static Logger logger = Logger.getLogger(ExecutionCallable.class);
    private JQLIssueVO issue;
    private Type type;
    private ExecutionIssueResultWapper result;
    public ExecutionCallable(JQLIssueVO issue, Type type, ExecutionIssueResultWapper result) {
        super();
        this.issue = issue;
        this.type = type;
        this.result = result;
    }

    @Override
    public Void call() throws Exception {
        try{
            if(type.equals(Type.TEST)){
                List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance().findTestExecutionInIsuee(issue.getKey()).getExecutions();
                if(executionIssues != null && !executionIssues.isEmpty()){
                    result.getExecutionsVO().addAll(executionIssues);
                }
            } else{
                List<ExecutionIssueVO> executionIssues = StoryUtility.getInstance().findAllTestExecutionInStory(issue);
                result.increasePland(issue.getFields().getCustomfield_14809());
                if(executionIssues != null && !executionIssues.isEmpty()){
                    result.getExecutionsVO().addAll(executionIssues);
                }
            }
        }catch (Exception e) {
            logger.error("error:", e);
        }
        return null;
    }

}
