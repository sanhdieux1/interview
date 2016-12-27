package models.gadget;

import java.util.Set;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import models.main.Release;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssigneeVsTestExecution implements Gadget {
    private final Gadget.Type type = Gadget.Type.ASSIGNEE_TEST_EXECUTION;

    private String user;
    private String id;
    @JsonProperty(required=true)
    private String projectName;
    private boolean selectAll;
    @JsonProperty(required=true)
    private Set<String> cycles;
    @JsonProperty(required=true)
    private Set<String> assignee;
    @JsonProperty(required=true)
    private Set<String> metrics;
    @JsonProperty(required=true)
    private Release release; // fixVersion

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

    @JsonIgnore
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

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

}
