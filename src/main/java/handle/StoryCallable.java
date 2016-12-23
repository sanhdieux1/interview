package handle;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import models.JQLIssueVO;
import models.StoryResultWapper;
import service.gadget.EpicUtility;

public class StoryCallable implements Callable<StoryResultWapper> {
    final static Logger logger = Logger.getLogger(StoryCallable.class);
    private String epic;
    
    public StoryCallable(String epic) {
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
