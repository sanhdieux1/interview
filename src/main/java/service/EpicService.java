package service;

import java.util.List;

import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueVO;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;

public interface EpicService {

    ExecutionsVO findAllExecutionIsuee2(String issueKey);

    List<JQLIssueVO> findAllIssues(String epic);

    List<ExecutionIssueVO> findAllTestExecutionIssue(String epic);

    List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget);

}
