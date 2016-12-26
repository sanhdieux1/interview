package models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssuefields {
    private JQLIssuetypeVO issuetype;
    private List<JQLIssueLinkVO> issuelinks;
    // Unpland
    private int customfield_14809;
    // Epic Parrent
    @JsonProperty("customfield_11209")
    private String epicLink;

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

    public String getEpicLink() {
        return epicLink;
    }

    public void setEpicLink(String epicLink) {
        this.epicLink = epicLink;
    }

}
