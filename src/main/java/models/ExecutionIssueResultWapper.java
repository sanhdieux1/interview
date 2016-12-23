package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionIssueResultWapper {
    private volatile List<ExecutionIssueVO> executionsVO = new ArrayList<>();
    private int planned;
    private String title;
    
    public void increasePland(int number){
        planned += number;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
