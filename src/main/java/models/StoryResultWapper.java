package models;

import java.util.List;

public class StoryResultWapper {
    private List<JQLIssueVO> result;
    private String epic;

    public List<JQLIssueVO> getResult() {
        return result;
    }

    public void setResult(List<JQLIssueVO> result) {
        this.result = result;
    }

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

}
