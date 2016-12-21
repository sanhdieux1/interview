package models.gadget;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssigneeVsTestExecution implements Gadget {
    private String id;
    private final Gadget.Type type = Gadget.Type.ASSIGNEE_TEST_EXECUTION;
    private List<String> columnAvaliable;
    @Override
    public String getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setColumnAvaliable(List<String> columnAvaliable) {
        this.columnAvaliable = columnAvaliable;
    }

    public void setId(String id) {
        this.id = id;
    }

    
}
