package models.gadget;

import java.util.List;

public interface Gadget {
    public long getId();

    public Type getType();

    public List<String> getColumnAvaliable();
    
    public enum Type {
        ASSIGNEE_TEST_EXECUTION, TEST_CYCLE_TEST_EXECUTION, EPIC_US_TEST_EXECUTION;
    }
}
