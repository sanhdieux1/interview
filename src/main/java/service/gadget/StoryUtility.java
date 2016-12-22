package service.gadget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import handle.ExecutionCallable;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueLinkVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.exception.MException;
import models.gadget.StoryVsTestExecution;

public class StoryUtility {
    private static StoryUtility INSTANCE = new StoryUtility();
    final static Logger logger = Logger.getLogger(StoryUtility.class);
    private StoryUtility() {
    }

    public static StoryUtility getInstance() {
        return INSTANCE;
    }

    public List<JQLIssueVO> findStoryInEpic(String epic) throws MException {
        List<JQLIssueVO> issues = EpicUtility.getInstance().findAllIssuesInEpicLink(epic);
        return issues.stream().filter(i -> JQLIssuetypeVO.Type.STORY.toString().equalsIgnoreCase(i.getFields().getIssuetype().getName()))
                .collect(Collectors.toList());
    }

    public List<ExecutionIssueVO> findAllTestExecutionInStory(JQLIssueVO issue) throws MException {
        List<ExecutionIssueVO> result = new ArrayList<>();
        if(JQLIssuetypeVO.Type.STORY.toString().equalsIgnoreCase(issue.getFields().getIssuetype().getName())){
            for (JQLIssueLinkVO issueLink : issue.getFields().getIssuelinks()){
                List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance().findTestExecutionInIsuee(issueLink.getId()).getExecutions();
                if(executionIssues != null && !executionIssues.isEmpty()){
                    result.addAll(executionIssues);
                }
            }
        }
        return result;
    }

    public void getDataStory(StoryVsTestExecution storyGadget) throws MException {
        List<String> stories = storyGadget.getStories();
        ExecutorService taskExecutor = Executors.newFixedThreadPool(stories.size());
        List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();
        Type type = JQLIssuetypeVO.Type.STORY;
        List<ExecutionIssueResultWapper> executionWapperList = new ArrayList<>();
        for (String story : stories){
            JQLIssueVO issue = GadgetUtility.getInstance().findIssue(story);
            ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
            resultWapper.increasePland(issue.getFields().getCustomfield_14809());
            executionWapperList.add(resultWapper);
            tasks.add(new ExecutionCallable(issue, type, resultWapper));
        }
        try{
            taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
            List<GadgetData> result = new ArrayList<>();
            for (ExecutionIssueResultWapper wapper : executionWapperList){
                GadgetData data = GadgetUtility.getInstance().convertToGadgetData(wapper);
                data.setUnplanned(wapper.getPlanned());
            }
        } catch (InterruptedException e){
            logger.error("can't execute thread", e);
            throw new MException("Timeout exeption");
        }
    }

}
