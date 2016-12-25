package util.gadget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import handle.ExecutionCallable;
import handle.StoryCallable;
import manament.log.LoggerWapper;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueLinkVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.StoryResultWapper;
import models.exception.APIException;
import models.gadget.StoryVsTestExecution;

public class StoryUtility {
    private static StoryUtility INSTANCE = new StoryUtility();
    final static LoggerWapper logger = LoggerWapper.getLogger(StoryUtility.class);
    private ConcurrentMap<String, List<JQLIssueVO>> storyInEpic = new ConcurrentHashMap<>();

    private StoryUtility() {
    }

    public static StoryUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, List<JQLIssueVO>> findStoryInEpic(List<String> epics) throws APIException {
        Map<String, List<JQLIssueVO>> storiesData = new HashMap<>();

        ExecutorService taskExecutor = Executors.newFixedThreadPool(epics.size());
        List<StoryCallable> tasks = new ArrayList<StoryCallable>();
        for (String epic : epics){
            if(storyInEpic.get(epic) == null || storyInEpic.get(epic).isEmpty()){
                tasks.add(new StoryCallable(epic));
            } else{
                storiesData.put(epic, storyInEpic.get(epic));
            }
        }
        List<Future<StoryResultWapper>> results;
        try{
            results = taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
            for (Future<StoryResultWapper> result : results){
                StoryResultWapper resultWapper = result.get();
                List<JQLIssueVO> stories = null;
                if(resultWapper.getResult() != null && !resultWapper.getResult().isEmpty()){
                    stories = filter(resultWapper.getResult(), JQLIssuetypeVO.Type.STORY);
                    storyInEpic.put(resultWapper.getEpic(), stories);
                }
                storiesData.put(resultWapper.getEpic(), stories != null ? stories : new ArrayList<>());
            }
        } catch (ExecutionException e){
            if(e.getCause() instanceof APIException){
                throw (APIException)e.getCause();
            }
            throw new APIException("error during invoke task", e);
        } catch (InterruptedException e){
            logger.fastInfo("Ignore findStoryInEpic task");
            logger.fastDebug("Error during invoke task", e);
            Thread.currentThread().interrupt(); // ignore/reset
        }
        return storiesData;
    }

    private List<JQLIssueVO> filter(List<JQLIssueVO> data, JQLIssuetypeVO.Type type) {
        return data.stream().filter(i -> type.toString().equalsIgnoreCase(i.getFields().getIssuetype().getName())).collect(Collectors.toList());
    }

    public List<ExecutionIssueVO> findAllTestExecutionInStory(JQLIssueVO issue) throws APIException {
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

    public List<GadgetData> getDataStory(StoryVsTestExecution storyGadget) throws APIException {
        List<GadgetData> storyDatas = new ArrayList<>();
        Set<String> stories = storyGadget.getStories();
        if(storyGadget.isSelectAll()){
            List<JQLIssueVO> storyIssues = findStoryInEpic(Arrays.asList(storyGadget.getEpic())).get(storyGadget.getEpic());
            stories = storyIssues.stream().map(t -> t.getKey()).collect(Collectors.toSet());
        }
        if(stories == null || stories.isEmpty()){
            return storyDatas;
        }
        ExecutorService taskExecutor = Executors.newFixedThreadPool(stories.size());
        List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();
        Type type = JQLIssuetypeVO.Type.STORY;
        List<ExecutionIssueResultWapper> executionWapperList = new ArrayList<>();
        for (String story : stories){
            
            JQLIssueVO issue = GadgetUtility.getInstance().findIssue(story);
            ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
            resultWapper.increasePland(issue.getFields().getCustomfield_14809());
            executionWapperList.add(resultWapper);
            tasks.add(new ExecutionCallable(issue, type));
        }
        try{
            List<Future<ExecutionIssueResultWapper>> results = taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
            for(Future<ExecutionIssueResultWapper> result :results){
                ExecutionIssueResultWapper wapper = result.get();
                GadgetData data = GadgetUtility.getInstance().convertToGadgetData(wapper);
                data.setUnplanned(wapper.getPlanned());
                data.setTitle(wapper.getTitle());
                storyDatas.add(data);
            }
            return storyDatas;
        }catch(ExecutionException e){
            if(e.getCause() instanceof APIException){
                throw (APIException)e.getCause();
            }
            throw new APIException("error during invoke", e);
        } catch (InterruptedException e){
            logger.fastDebug("error during invoke", e);
            throw new APIException("error during invoke", e);
        }
    }

}
