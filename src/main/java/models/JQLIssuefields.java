package models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssuefields {
    private JQLIssuetypeVO issuetype;
    private List<JQLIssueLinkVO> issuelinks;
    private int customfield_14809;

    public JQLIssuetypeVO getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(JQLIssuetypeVO issuetype) {
        this.issuetype = issuetype;
    }

    public List<JQLIssueLinkVO> getIssuelinks() {
        return issuelinks;
    }

    public void setIssuelinks(List<JQLIssueLinkVO> issuelinks) {
        this.issuelinks = issuelinks;
    }

    public int getCustomfield_14809() {
        return customfield_14809;
    }

    public void setCustomfield_14809(int customfield_14809) {
        this.customfield_14809 = customfield_14809;
    }

}
