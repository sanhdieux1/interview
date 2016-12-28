package models.gadget;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import models.main.Release;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryVsTestExecution implements Gadget {
    private String id;
    private Type type = Type.STORY_TEST_EXECUTION;
    private String projectName;
    private Release release; // fixVersion
    private List<String> metrics;
    private Set<String> epic;
    private Set<String> stories;
    private boolean selectAllStory;
    private boolean selectAllEpic;
    private String user;
    
    public boolean isSelectAllEpic() {
        return selectAllEpic;
    }

    public void setSelectAllEpic(boolean selectAllEpic) {
        this.selectAllEpic = selectAllEpic;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public Set<String> getEpic() {
        return epic;
    }

    public void setEpic(Set<String> epic) {
        this.epic = epic;
    }   
    
    public Set<String> getStories() {
        return stories;
    }

    public void setStories(Set<String> stories) {
        this.stories = stories;
    }

    public boolean isSelectAllStory() {
        return selectAllStory;
    }

    public void setSelectAllStory(boolean selectAll) {
        this.selectAllStory = selectAll;
    }

}
