package util.gadget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import handle.FindIssueInEpicCallable;
import handle.executors.TestExecutionCallable;
import handle.executors.ExecutorManagement;
import handle.executors.FindIssueCallable;
import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueLinkVO;
import models.JQLIssueVO;
import models.JQLIssueWapper;
import models.JQLIssuefields;
import models.JQLIssuetypeVO;
import models.JQLIssuetypeVO.Type;
import models.exception.APIException;
import models.gadget.StoryVsTestExecution;
import models.main.GadgetData;
import models.main.GadgetDataWapper;
import models.main.Release;
import util.Constant;
import util.PropertiesUtil;

public class StoryUtility {
    private static StoryUtility INSTANCE = new StoryUtility();
    private static final LoggerWapper logger = LoggerWapper.getLogger(StoryUtility.class);
    private static final String INWARD_TEST_BY = "Is a test by";
    private ConcurrentMap<String, JQLIssueWapper> storyInEpic = new ConcurrentHashMap<>();

    private StoryUtility() {
    }

    public static StoryUtility getInstance() {
        return INSTANCE;
    }

    public Map<String, JQLIssueWapper> findStoryInEpic(List<String> epics) throws APIException {
        Map<String, JQLIssueWapper> storiesData = new HashMap<>();
        List<FindIssueInEpicCallable> tasks = new ArrayList<FindIssueInEpicCallable>();
        for (String epic : epics){
            JQLIssueVO epicIssue = GadgetUtility.getInstance().findIssue(epic);
            if(storyInEpic.get(epic) == null){
                tasks.add(new FindIssueInEpicCallable(epicIssue));
            } else{
                storiesData.put(epic, storyInEpic.get(epic));
            }
        }
        List<JQLIssueWapper> results = ExecutorManagement.getInstance().getResult(ExecutorManagement.getInstance().invokeTask(tasks));
        if(results != null && !results.isEmpty())
            for (JQLIssueWapper resultWapper : results){
                if(resultWapper != null){
                    Set<JQLIssueVO> stories = null;
                    if(resultWapper.getChild() != null && !resultWapper.getChild().isEmpty()){
                        stories = filter(resultWapper.getChild(), JQLIssuetypeVO.Type.STORY);
                        resultWapper.setChild(stories);
                        storyInEpic.put(resultWapper.getIssue().getKey(), resultWapper);
                        storiesData.put(resultWapper.getIssue().getKey(), resultWapper);
                    }
                }
            }
        return storiesData;
    }

    private Set<JQLIssueVO> filter(Set<JQLIssueVO> set, JQLIssuetypeVO.Type type) {
        return set.stream().filter(i -> type.toString().equalsIgnoreCase(i.getFields().getIssuetype().getName())).collect(Collectors.toSet());
    }

    public List<ExecutionIssueVO> findAllTestExecutionInStory(JQLIssueVO issue) throws APIException {
        List<ExecutionIssueVO> result = new ArrayList<>();
        if(JQLIssuetypeVO.Type.STORY.toString().equalsIgnoreCase(issue.getFields().getIssuetype().getName())){
            List<JQLIssueLinkVO> issueLinks = findAllTestIssueForStory(issue);
            String test = "";
            for (JQLIssueLinkVO t : issueLinks){
                test = test + (t.getInwardIssue().getKey()) + ",";
            }
            if(issueLinks != null && !issueLinks.isEmpty()){
                for (JQLIssueLinkVO issueLink : issueLinks){
                    List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance().findTestExecutionInIsuee(issueLink.getInwardIssue().getKey());
                    if(executionIssues != null && !executionIssues.isEmpty()){
                        result.addAll(executionIssues);
                    }
                }
            }
        }
        return result;
    }

    public List<JQLIssueLinkVO> findAllTestIssueForStory(JQLIssueVO issue) {
        List<JQLIssueLinkVO> testIssue = null;
        if(issue != null && issue.getFields() != null && issue.getFields().getIssuelinks() != null){
            List<JQLIssueLinkVO> issueLinks = issue.getFields().getIssuelinks();
            testIssue = issueLinks.stream().filter(i -> INWARD_TEST_BY.equals(i.getType().getInward())).collect(Collectors.toList());
        } else{
            logger.fasttrace("cannot findout issuelinks of %s", issue);
        }
        return testIssue;
    }

    public Map<String, GadgetDataWapper> getDataStory(StoryVsTestExecution storyGadget) throws APIException {
        Map<String, GadgetDataWapper> returnData = new HashMap<>();
        Map<String, JQLIssueWapper> epicWrapperMap = null;
        if(storyGadget.isSelectAllEpic() && storyGadget.isSelectAllStory()){
            String project = storyGadget.getProjectName();
            Release release = storyGadget.getRelease();
            Set<APIIssueVO> epicIssues = EpicUtility.getInstance().getEpicLinks(project, release.toString(), storyGadget.getProducts());
            Set<String> epics = epicIssues.stream().map(e -> e.getKey()).collect(Collectors.toSet());
            epicWrapperMap = findStoryInEpic(new ArrayList<String>(epics));
        } else if(storyGadget.isSelectAllStory()){
            epicWrapperMap = findStoryInEpic(new ArrayList<String>(storyGadget.getEpic()));
        } else{
            Set<JQLIssueVO> storyIssues = new HashSet<>();
            Set<String> stories = storyGadget.getStories();
            List<FindIssueCallable> tasks = new ArrayList<FindIssueCallable>();
            stories.forEach(s -> tasks.add(new FindIssueCallable(s)));

            List<Future<JQLIssueVO>> taskResult = ExecutorManagement.getInstance().invokeTask(tasks);
            List<JQLIssueVO> storyIssuesList = ExecutorManagement.getInstance().getResult(taskResult);
            if(storyIssuesList != null){
                storyIssues.addAll(storyIssuesList);
            }
            Map<String, Set<JQLIssueVO>> epicMap = storyIssues.stream()
                    .collect(Collectors.groupingBy(s -> s.getFields().getEpicLink() != null ? s.getFields().getEpicLink() : "", Collectors.toSet()));
            epicWrapperMap = new HashMap<>();
            for(String epicKey : epicMap.keySet()){
                JQLIssueVO epicIssue = GadgetUtility.getInstance().findIssue(epicKey);
                epicWrapperMap.put(epicKey, new JQLIssueWapper(epicIssue, epicMap.get(epicKey)));
            }
        }

        if(epicWrapperMap == null || epicWrapperMap.isEmpty()){
            return returnData;
        }
        Type type = JQLIssuetypeVO.Type.STORY;
        for (String epic : epicWrapperMap.keySet()){
            List<TestExecutionCallable> tasks = new ArrayList<TestExecutionCallable>();
            JQLIssueWapper storyWapper = epicWrapperMap.get(epic);
            storyWapper.getChild().forEach(s -> tasks.add(new TestExecutionCallable(s, type)));
            List<ExecutionIssueResultWapper> results = ExecutorManagement.getInstance().invokeAndGet(tasks);
            
            List<GadgetData> storyDatas = new ArrayList<>();
            for (ExecutionIssueResultWapper wapper : results){
                GadgetData data = GadgetUtility.getInstance().convertToGadgetData(wapper.getExecutionsVO());
                data.setPlanned(wapper.getPlanned());
                data.setKey(wapper.getIssue());
                storyDatas.add(data);
            }
            GadgetDataWapper dataWrapper = new GadgetDataWapper();
            dataWrapper.setIssueData(storyDatas);
            dataWrapper.setSummary(storyWapper.getIssue().getFields().getSummary());
            returnData.put(epic, dataWrapper);
        }
        return returnData;
    }

}
