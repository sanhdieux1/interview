package service;

import java.util.List;

import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueVO;
import models.exception.MException;
import models.gadget.EpicVsTestExecution;
import models.main.ExecutionsVO;

public interface EpicService {

    ExecutionsVO findTestExecutionInIsuee(String issueKey);

    List<JQLIssueVO> findAllIssuesInEpicLink(String epic);

    ExecutionIssueResultWapper findAllExecutionIssueInEpic(String epic);

    List<GadgetData> getDataEPic(EpicVsTestExecution epicGadget);

    List<ExecutionIssueVO> findAllTestExecutionInStory(JQLIssueVO issue);

}
