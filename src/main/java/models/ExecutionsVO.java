package models;

import java.util.List;


public class ExecutionsVO {
    private List<IssueVO> executions;
    private int currentIndex;
    private int maxResultAllowed;
    private List<Integer> linksNew;
    private int totalCount;
    private List<String> executionIds;
    public int getCurrentIndex() {
        return currentIndex;
    }
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
    public int getMaxResultAllowed() {
        return maxResultAllowed;
    }
    public void setMaxResultAllowed(int maxResultAllowed) {
        this.maxResultAllowed = maxResultAllowed;
    }
    public List<Integer> getLinksNew() {
        return linksNew;
    }
    public void setLinksNew(List<Integer> linksNew) {
        this.linksNew = linksNew;
    }
    public List<IssueVO> getExecutions() {
        return executions;
    }
    public void setExecutions(List<IssueVO> executions) {
        this.executions = executions;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public List<String> getExecutionIds() {
        return executionIds;
    }
    public void setExecutionIds(List<String> executionIds) {
        this.executionIds = executionIds;
    }
    
}
