package handle;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import manament.log.LoggerWapper;
import models.JQLIssueVO;
import models.JQLIssueWapper;
import util.gadget.EpicUtility;

public class FindIssueInEpicCallable implements Callable<JQLIssueWapper> {
    final static LoggerWapper logger = LoggerWapper.getLogger(FindIssueInEpicCallable.class);
    private JQLIssueVO epic;

    public FindIssueInEpicCallable(JQLIssueVO epic) {
        this.epic = epic;
    }

    @Override
    public JQLIssueWapper call() throws Exception {
        JQLIssueWapper resultWapper = new JQLIssueWapper();
        resultWapper.setIssue(epic);
        List<JQLIssueVO> issueInEpic = EpicUtility.getInstance().findAllIssuesInEpicLink(epic.getKey());
        if(issueInEpic != null){
            resultWapper.setChild(new HashSet<JQLIssueVO>(issueInEpic));
        }
        return resultWapper;
    }

}
