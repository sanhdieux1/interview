package handle.executors;

import java.util.concurrent.Callable;

import models.JQLIssueVO;
import util.gadget.GadgetUtility;

public class FindIssueCallable implements Callable<JQLIssueVO> {
    private String issueKey;
    public FindIssueCallable(String key) {
        issueKey = key;
    }

    @Override
    public JQLIssueVO call() throws Exception {
        return GadgetUtility.getInstance().findIssue(issueKey);
    }

}
