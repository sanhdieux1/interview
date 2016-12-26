package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionIssueResultWapper {
    private volatile List<ExecutionIssueVO> executionsVO = new ArrayList<>();
    private int planned;
    private APIIssueVO issue;

    public void increasePland(int number) {
        planned += number;
    }

    public APIIssueVO getIssue() {
        return issue;
    }

    public void setIssue(APIIssueVO issue) {
        this.issue = issue;
    }

    public List<ExecutionIssueVO> getExecutionsVO() {
        return executionsVO;
    }

    public void setExecutionsVO(List<ExecutionIssueVO> executionsVO) {
        this.executionsVO = executionsVO;
    }

    public int getPlanned() {
        return planned;
    }

    public void setPlanned(int planned) {
        this.planned = planned;
    }

}
