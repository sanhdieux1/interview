package models.gadget;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssigneeVsTestExecution implements Gadget {
    private final Gadget.Type type = Gadget.Type.ASSIGNEE_TEST_EXECUTION;
    private String user;
    private String id;
    private String projectName;
    private boolean selectAll;
    private Set<String> cycles;
    private Set<String> assignee;
    private Set<String> metrics;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public Set<String> getCycles() {
        return cycles;
    }

    public void setCycles(Set<String> cycles) {
        this.cycles = cycles;
    }

    public Set<String> getAssignee() {
        return assignee;
    }

    public void setAssignee(Set<String> assignee) {
        this.assignee = assignee;
    }

    public Set<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<String> metrics) {
        this.metrics = metrics;
    }

}
