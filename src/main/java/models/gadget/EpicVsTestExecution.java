package models.gadget;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpicVsTestExecution implements Gadget {

	private String id;
    private Gadget.Type type = Gadget.Type.EPIC_US_TEST_EXECUTION;
    private String cycleName;
    private String projectName;
    //fixVersion
    private String release;
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

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
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
