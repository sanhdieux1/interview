package models.gadget;

public interface Gadget {
    public String getId();

    public Type getType();

    public enum Type {
        ASSIGNEE_TEST_EXECUTION, TEST_CYCLE_TEST_EXECUTION, EPIC_US_TEST_EXECUTION;
    }
}
