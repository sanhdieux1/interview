package models.gadget;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import models.main.Release;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpicVsTestExecution implements Gadget {

	private String user;
	private String id;
	private Gadget.Type type = Gadget.Type.EPIC_US_TEST_EXECUTION;
	private String projectName;
	// fixVersion
	private Release release;
	private List<String> metrics;
	private List<String> epic;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Gadget.Type getType() {
		return type;
	}

	public void setType(Gadget.Type type) {
		this.type = type;
	}

	public String getUser() {
		return user;
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

	public List<String> getEpic() {
		return epic;
	}

	public void setEpic(List<String> epic) {
		this.epic = epic;
	}

}