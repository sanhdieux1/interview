package models.gadget;

import java.util.List;

public class EpicVsTestExecution {
    private long id;
    private Gadget.Type type;
    private List<String> columnList;
    private String cycleName;
    private String projectName;
    //fixVersion
    private String release;
    private List<String> metrics;
    private List<String> epic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Gadget.Type getType() {
        return type;
    }

    public void setType(Gadget.Type type) {
        this.type = type;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
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
