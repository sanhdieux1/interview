package models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssuefields {
    private JQLIssuetypeVO issuetype;
    private List<JQLIssueLinkVO> issuelinks;

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

}
