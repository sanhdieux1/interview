package models;

public class GadgetData {
    private String title;
    private int unexecuted;
    private int failed;
    private int wip;
    private int blocked;
    private int passed;
    private int planned;
    private int unplanned;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnexecuted() {
        return unexecuted;
    }

    public void setUnexecuted(int unexecuted) {
        this.unexecuted = unexecuted;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getWip() {
        return wip;
    }

    public void setWip(int wip) {
        this.wip = wip;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getPlanned() {
        return planned;
    }

    public void setPlanned(int planned) {
        this.planned = planned;
    }

    public int getUnplanned() {
        return unplanned;
    }

    public void setUnplanned(int unplanned) {
        this.unplanned = unplanned;
    }

}