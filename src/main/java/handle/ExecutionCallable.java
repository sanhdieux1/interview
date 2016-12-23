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

public class ExecutionCallable implements Callable<ExecutionIssueResultWapper> {
    final static Logger logger = Logger.getLogger(ExecutionCallable.class);
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
        try{
            resultWapper.setTitle(issue.getKey());
            if(type.equals(Type.TEST)){
                List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance().findTestExecutionInIsuee(issue.getKey()).getExecutions();
                if(executionIssues != null && !executionIssues.isEmpty()){
                    resultWapper.getExecutionsVO().addAll(executionIssues);
                }
            } else{
                List<ExecutionIssueVO> executionIssues = StoryUtility.getInstance().findAllTestExecutionInStory(issue);
                resultWapper.setPlanned(issue.getFields().getCustomfield_14809());
                if(executionIssues != null && !executionIssues.isEmpty()){
                    resultWapper.getExecutionsVO().addAll(executionIssues);
                }
            }
        }catch (Exception e) {
            logger.error("error:", e);
        }
        return resultWapper;
    }

}
