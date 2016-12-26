package util.gadget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import handle.ExecutionCallable;
import handle.FindIssueCallable;
import handle.StoryCallable;
import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.GadgetData;
import models.JQLIssueLinkVO;
import models.JQLIssueVO;
import models.JQLIssuefields;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.StoryResultWapper;
import models.exception.APIException;
import models.gadget.StoryVsTestExecution;

public class StoryUtility {
    private static StoryUtility INSTANCE = new StoryUtility();
    final static LoggerWapper logger = LoggerWapper.getLogger(StoryUtility.class);
    private ConcurrentMap<String, Set<JQLIssueVO>> storyInEpic = new ConcurrentHashMap<>();

    private StoryUtility() {
    }

    public static StoryUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, Set<JQLIssueVO>> findStoryInEpic(List<String> epics) throws APIException {
        Map<String, Set<JQLIssueVO>> storiesData = new HashMap<>();

        ExecutorService taskExecutor = Executors.newFixedThreadPool(epics.size());
        List<StoryCallable> tasks = new ArrayList<StoryCallable>();
        for (String epic : epics){
            if(storyInEpic.get(epic) == null || storyInEpic.get(epic).isEmpty()){
                tasks.add(new StoryCallable(epic));
            } else{
                storiesData.put(epic, storyInEpic.get(epic).stream().collect(Collectors.toSet()));
            }
        }
        List<Future<StoryResultWapper>> results;
        try{
            results = taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
            for (Future<StoryResultWapper> result : results){
                StoryResultWapper resultWapper = result.get();
                Set<JQLIssueVO> stories = null;
                if(resultWapper.getResult() != null && !resultWapper.getResult().isEmpty()){
                    stories = filter(resultWapper.getResult(), JQLIssuetypeVO.Type.STORY);
                    storyInEpic.put(resultWapper.getEpic(), stories);
                }
                storiesData.put(resultWapper.getEpic(), stories != null ? stories : new HashSet<>());
            }
        } catch (ExecutionException e){
            if(e.getCause() instanceof APIException){
                throw (APIException) e.getCause();
            }
            throw new APIException("error during invoke task", e);
        } catch (InterruptedException e){
            logger.fastInfo("Ignore findStoryInEpic task");
            logger.fastDebug("Error during invoke task", e);
            Thread.currentThread().interrupt(); // ignore/reset
        }
        return storiesData;
    }

    private Set<JQLIssueVO> filter(List<JQLIssueVO> data, JQLIssuetypeVO.Type type) {
        return data.stream().filter(i -> type.toString().equalsIgnoreCase(i.getFields().getIssuetype().getName())).collect(Collectors.toSet());
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

    public Map<String, List<GadgetData>> getDataStory(StoryVsTestExecution storyGadget) throws APIException {
        Map<String, List<GadgetData>> returnData = new HashMap<>();
        Map<String, Set<JQLIssueVO>> epicMap = null;
        if(storyGadget.isSelectAll()){
            epicMap = findStoryInEpic(new ArrayList<String>(storyGadget.getEpic()));
        } else{
            Set<JQLIssueVO> storyIssues = new HashSet<>();
            Set<String> stories = storyGadget.getStories();
                List<FindIssueCallable> tasks = new ArrayList<FindIssueCallable>();
                stories.forEach(s -> tasks.add(new FindIssueCallable(s)));
                
            ExecutorService taskExecutor = Executors.newCachedThreadPool();
            try{
                List<Future<JQLIssueVO>> result = taskExecutor.invokeAll(tasks);
                for (Future<JQLIssueVO> re : result){
                    storyIssues.add(re.get());
                }
                taskExecutor.shutdown();
            } catch (ExecutionException e){
                if(e.getCause() instanceof APIException){
                    throw (APIException) e.getCause();
                }
                throw new APIException("error during invoke", e);
            } catch (InterruptedException e){
                logger.fastDebug("error during invoke", e);
                throw new APIException("error during invoke", e);
            }
            epicMap = storyIssues.stream().collect(Collectors.groupingBy(s -> s.getFields().getEpicLink(), Collectors.toSet()));
        }
        
        if(epicMap == null || epicMap.isEmpty()){
            return returnData;
        }
        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        Type type = JQLIssuetypeVO.Type.STORY;
        Map<String, List<ExecutionCallable>> taskMap = new HashMap<>();
        for (String epic : epicMap.keySet()){
            List<ExecutionCallable> tasks = new ArrayList<ExecutionCallable>();
            Set<JQLIssueVO> story = epicMap.get(epic);
            story.forEach(s -> tasks.add(new ExecutionCallable(s, type)));
            taskMap.put(epic, tasks);
        }
        for (String epic : taskMap.keySet()){
            List<GadgetData> storyDatas = new ArrayList<>();
            List<ExecutionCallable> tasks = taskMap.get(epic);
            try{
                List<Future<ExecutionIssueResultWapper>> results = taskExecutor.invokeAll(tasks);
                taskExecutor.shutdown();
                for (Future<ExecutionIssueResultWapper> result : results){
                    ExecutionIssueResultWapper wapper = result.get();
                    GadgetData data = GadgetUtility.getInstance().convertToGadgetData(wapper);
                    data.setUnplanned(wapper.getPlanned());
                    data.setKey(wapper.getIssue());
                    storyDatas.add(data);
                }
                returnData.put(epic, storyDatas);
            } catch (ExecutionException e){
                if(e.getCause() instanceof APIException){
                    throw (APIException) e.getCause();
                }
                throw new APIException("error during invoke", e);
            } catch (InterruptedException e){
                logger.fastDebug("error during invoke", e);
                throw new APIException("error during invoke", e);
            }
        }
        return returnData;
    }

}
