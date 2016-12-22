package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionIssueResultWapper {
    private volatile List<ExecutionIssueVO> executionsVO = Collections.synchronizedList(new ArrayList<>());
    private AtomicInteger planned = new AtomicInteger(0);
    
    public void increasePland(int number){
        planned.addAndGet(number);
    }
    
    public List<ExecutionIssueVO> getExecutionsVO() {
        return executionsVO;
    }
    public void setExecutionsVO(List<ExecutionIssueVO> executionsVO) {
        this.executionsVO = executionsVO;
    }

    public Integer getPlanned() {
        return planned.get();
    }

    public void setPlanned(Integer planned) {
        this.planned = new AtomicInteger(planned);
    }
    
}
