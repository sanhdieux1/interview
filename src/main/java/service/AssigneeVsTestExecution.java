package service;

import java.util.List;

public class AssigneeVsTestExecution implements Gadget {
    private long id;
    private final Gadget.Type type = Gadget.Type.ASSIGNEE_TEST_EXECUTION;
    private List<String> columnAvaliable;
    @Override
    public long getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<String> getColumnAvaliable() {
        return columnAvaliable;
    }
    
    public void setColumnAvaliable(List<String> columnAvaliable) {
        this.columnAvaliable = columnAvaliable;
    }

    public void setId(long id) {
        this.id = id;
    }

    
}
