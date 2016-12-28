package handle;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import manament.log.LoggerWapper;
import models.JQLIssueVO;
import models.StoryResultWapper;
import util.gadget.EpicUtility;

public class FindIssueInEpicCallable implements Callable<StoryResultWapper> {
    final static LoggerWapper logger = LoggerWapper.getLogger(FindIssueInEpicCallable.class);
    private String epic;
    
    public FindIssueInEpicCallable(String epic) {
        this.epic = epic;
    }

    @Override
    public StoryResultWapper call() throws Exception {
        StoryResultWapper resultWapper = new StoryResultWapper();
        resultWapper.setEpic(epic);
        resultWapper.setResult(EpicUtility.getInstance().findAllIssuesInEpicLink(epic));
        return resultWapper;
    }

}
