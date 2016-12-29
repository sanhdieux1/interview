package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APIIssueVO {
    private String key;
    private String self;
    private String summary;

    public APIIssueVO() {
    }

    public APIIssueVO(String key, String self) {
        this.key = key;
        this.self = self;
    }
    
    public APIIssueVO(String key, String self, String summary) {
        this.key = key;
        this.self = self;
        this.summary = summary;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
