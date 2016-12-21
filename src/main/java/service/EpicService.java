package service;

import java.util.List;

import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueVO;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;

public interface EpicService {

    ExecutionsVO findExecutionIsuee(String issueKey);

    List<JQLIssueVO> findAllIssues(String epic);

    List<ExecutionIssueVO> findAllExecutionIssue(String epic);

    List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget);

    List<ExecutionIssueVO> findAllExecutionIsueeInStory(JQLIssueVO issue);

}
